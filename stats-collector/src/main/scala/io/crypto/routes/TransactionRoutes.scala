package io.crypto.routes

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.jdk.DurationConverters._

import akka.actor.typed._
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import de.heikoseeberger.akkahttpziojson.ZioJsonSupport._
import io.crypto._
import io.crypto.services.TransactionCollector._
import java.math.BigInteger
import java.util.concurrent.TimeUnit

class TransactionRoutes(transactionCollector: ActorRef[Command], routeConfig: CollectorConfig.RouteConfig)(implicit
val system: ActorSystem[_]) {

  implicit private val timeout =
    Timeout.create(FiniteDuration(routeConfig.askTimeout.toMillis, TimeUnit.MILLISECONDS).toJava)

  def getAvgGasPrice(): Future[BigInteger] = transactionCollector.ask(AvgPendingGasPrice)

  val routes: Route =
    pathPrefix("transactions") {
      concat(
        pathEnd {
          concat(
            get {
              complete(getAvgGasPrice())
            }
          )
        }
      )
    }
}
