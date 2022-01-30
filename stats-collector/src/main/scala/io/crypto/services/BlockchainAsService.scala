package io.crypto.services

import scala.jdk.FunctionConverters._

import akka.actor.typed._
import akka.actor.typed.scaladsl._
import com.typesafe.scalalogging.StrictLogging
import io.crypto.CollectorConfig._
import io.crypto.services.TransactionCollector._
import java.net.URI
import org.web3j.protocol.Web3j
import org.web3j.protocol.websocket._

case class Web3jModule(web3j: Web3j, wssService: WebSocketService)

object BlockchainAsService extends StrictLogging {
  sealed trait Command
  case object Connect extends Command
  case object Disconnect extends Command

  def apply(config: BlockchainServiceConfig, collector: ActorRef[Transaction]) = {
    val webSocketClient = new WebSocketClient(new URI(s"${config.wssUrl}${config.token}"))
    val webSocketService = new WebSocketService(webSocketClient, false)
    val web3j = Web3j.build(webSocketService)
    listen(Web3jModule(web3j, webSocketService), collector)
  }

  private def listen(module: Web3jModule, collector: ActorRef[Transaction]): Behavior[Command] = {
    Behaviors.receive { (context, command) =>
      command match {
        case Connect =>
          connect(module, context)
          subscribeToPending(module, collector)
          Behaviors.same
        case Disconnect =>
          module.wssService.close()
          module.web3j.shutdown()
          Behaviors.same
      }
    }
  }

  private def connect(module: Web3jModule, context: ActorContext[Command]) = {
    module.wssService.connect(
      ((msg: String) => ()),
      ((err: Throwable) => {
        logger.error(s"Error while listening to websocker", err)
        logger.info(s"Reconnecting ...")
        (context.self ! Connect)
      }).asJava,
      () => {
        logger.info(s"Disconecting ...")
      }
    )
  }

  private def subscribeToPending(module: Web3jModule, collector: ActorRef[Transaction]) = {
    module.web3j.transactionFlowable().subscribe(
      ((pending) => {
        collector ! Transaction(pending)
      }),
      ((error: Throwable) =>
        println(s"Error ?!", error))
    )
  }
}
