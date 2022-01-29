package io.crypto

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

import scala.util.Failure
import scala.util.Success

object CollectorApp extends App {
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext
    val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  val rootBehavior = Behaviors.setup[Nothing] { context =>
    val userRegistryActor = context.spawn(UserRegistry(), "UserRegistryActor")
    val config = zio.Runtime.unsafeFromLayer(CollectorConfig.readLayer).unsafeRun(CollectorConfig.service)
    context.watch(userRegistryActor)

    val routes = new UserRoutes(userRegistryActor, config.routes)(context.system)
    startHttpServer(routes.userRoutes)(context.system)

    Behaviors.empty
  }

  val system = ActorSystem[Nothing](rootBehavior, "CollectorsAppHttpServer")
}
