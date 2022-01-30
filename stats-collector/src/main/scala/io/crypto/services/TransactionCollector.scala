package io.crypto.services

import scala.concurrent.duration._

import akka.actor.typed._
import akka.actor.typed.scaladsl._
import io.crypto.common.Codecs._
import io.crypto.models._
import java.math.BigInteger
import org.joda.time.DateTime
import org.web3j.protocol.core.methods.response.{Transaction => Web3jTransaction}
import zio.json._
object TransactionCollector {
  val initGranulation = 1.minute

  case class Collected(
    transactions: Long = 0,
    avgGasPrice: BigInteger = BigInteger.valueOf(0),
    from: DateTime = DateTime.now(),
    granulation: FiniteDuration = initGranulation
  )

  object Collected {
    implicit val decoder: JsonDecoder[Collected] = DeriveJsonDecoder.gen[Collected]
    implicit val encoder: JsonEncoder[Collected] = DeriveJsonEncoder.gen[Collected]
  }

  case class CurrentValue(
    transactions: Long = 0,
    totalGas: BigInteger = BigInteger.valueOf(0),
    from: DateTime = DateTime.now()
  )

  def apply(): Behavior[Command] = {
    Behaviors.withTimers { timers =>
      timers.startSingleTimer(Save, initGranulation)
      collect(timers)
    }
  }

  private def collect(
    timers: TimerScheduler[Command],
    collected: Vector[Collected] = Vector.empty,
    current: CurrentValue = CurrentValue(),
    granulation: FiniteDuration = initGranulation
  ): Behavior[Command] = {
    Behaviors.receiveMessage {
      case transaction: Transaction                =>
        collect(
          timers,
          collected,
          current.copy(
            transactions = current.transactions + 1,
            totalGas = current.totalGas.add(transaction.gasPrice)
          )
        )
      case Save                                    =>
        timers.startSingleTimer(Save, granulation)
        collect(
          timers,
          collected :+ Collected(
            current.transactions,
            current.totalGas.divide(BigInteger.valueOf(current.transactions)),
            current.from,
            granulation
          ),
          CurrentValue(),
          granulation
        )
      case Reset(granulation: FiniteDuration, ref) =>
        collect(timers, collected, current, granulation)
      case GetTransactionsStats(ref)               =>
        ref ! collected
        Behaviors.same
    }
  }

  // COMMANDS
  sealed trait Command

  final case class Transaction(blockHash: String, blockNumber: BigInteger, gasPrice: BigInteger)      extends Command
  final case object Save                                                                              extends Command
  final case class Reset(granulation: FiniteDuration = initGranulation, replyTo: ActorRef[Confirmed]) extends Command
  final case class GetTransactionsStats(replyTo: ActorRef[Vector[Collected]])                         extends Command

  final case class Confirmed(message: String)

  object Confirmed   {
    implicit val encoder: JsonEncoder[Confirmed] = DeriveJsonEncoder.gen
  }
  object Transaction {
    def apply(transaction: Web3jTransaction): Transaction = {
      Transaction(transaction.getBlockHash(), transaction.getBlockNumber(), transaction.getGasPrice())
    }
  }
}
