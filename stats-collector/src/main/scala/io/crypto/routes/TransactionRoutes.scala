package io.crypto.routes

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.jdk.DurationConverters._

import akka.actor.typed._
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import de.heikoseeberger.akkahttpziojson.ZioJsonSupport
import io.crypto._
import io.crypto.common.Codecs._
import io.crypto.services.TransactionCollector._
import java.math.BigInteger
import java.util.concurrent.TimeUnit
import zio.json._

case class ResetRequest(value: FiniteDuration)

class TransactionRoutes(transactionCollector: ActorRef[Command], routeConfig: CollectorConfig.RouteConfig)(implicit
val system: ActorSystem[_])
    extends ZioJsonSupport {

  implicit private val resetRequestDecoder: JsonDecoder[ResetRequest] = DeriveJsonDecoder.gen[ResetRequest]

  implicit private val timeout =
    Timeout.create(FiniteDuration(routeConfig.askTimeout.toMillis, TimeUnit.MILLISECONDS).toJava)

  def getAvgGasPrice(): Future[Vector[Collected]]           = transactionCollector.ask(GetTransactionsStats)
  def setDuration(value: FiniteDuration): Future[Confirmed] = transactionCollector.ask(Reset(value, _))

  val routes: Route =
    pathPrefix("transactions") {
      concat(
        pathEnd {
          concat(
            get {
              complete(getAvgGasPrice())
            },
            post(
              decodeRequest {
                entity(as[ResetRequest]) { reset =>
                  complete(setDuration(reset.value))
                }
              }
            )
          )

        }
      )
    }
}
