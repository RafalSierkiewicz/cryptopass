package io.crypto

import scala.util._

import akka.actor.typed._
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.crypto.routes._
import io.crypto.services._

object CollectorApp extends App {
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext
    val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex)      =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  private def onInit(blockchainService: ActorRef[BlockchainAsService.Command])      = {
    blockchainService ! BlockchainAsService.Connect
  }

  val rootBehavior = Behaviors.setup[Nothing] { context =>
    val userRegistryActor    = context.spawn(UserRegistry(), "UserRegistryActor")
    val config               = zio.Runtime.unsafeFromLayer(CollectorConfig.readLayer).unsafeRun(CollectorConfig.service)
    val transactionCollector = context.spawn(TransactionCollector(), "TransactionCollector")
    val blockchainService    =
      context.spawn(BlockchainAsService(config.blockchainService, transactionCollector), "BlockchainAsService")

    context.watch(userRegistryActor)
    context.watch(blockchainService)

    val uRoutes = new UserRoutes(userRegistryActor, config.routes)(context.system)
    val tRoutes = new TransactionRoutes(transactionCollector, config.routes)(context.system)
    onInit(blockchainService)
    startHttpServer(uRoutes.userRoutes ~ tRoutes.routes)(context.system)

    Behaviors.empty
  }

  val system = ActorSystem[Nothing](rootBehavior, "CollectorsAppHttpServer")
}
