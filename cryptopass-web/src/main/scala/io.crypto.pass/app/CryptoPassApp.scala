package io.crypto.pass.app

import zhttp.http._
import zhttp.service.Server
import zio._

class CryptoPassApp {

  val run =
    Server.start(8090, routes(AppModule.make)).exitCode

  private def routes(module: Module) = {
    val testRoute = Http.collect[Request] {
      case Method.GET -> Root / "text" => Response.text("Hello Worl d ")
    }
    testRoute ++ module.passwordController.routes
  }
}

object CryptoPassApp extends zio.App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    new CryptoPassApp().run
}
