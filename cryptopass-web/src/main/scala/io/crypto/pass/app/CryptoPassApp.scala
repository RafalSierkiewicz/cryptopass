package io.crypto.pass.app

import zhttp.http._
import zhttp.service.Server
import zio._
import zio.config._
import zio.config.typesafe._
import java.io.File
import io.crypto.pass.config.AppConfig

class CryptoPassApp {
  val layer = AppConfig.readLayer

  val run =
    Server.start(8090, routes(AppModule.make).provideCustomLayer(layer)).exitCode

  private def routes(module: Module) = {
    val testRoute = Http.collectZIO[Request] {
      case Method.GET -> Root / "text" => ZIO.succeed(Response.text("Hello Worl d "))
    }
    testRoute ++ module.passwordController.routes
  }
}

object CryptoPassApp extends ZIOAppDefault {
  def run = new CryptoPassApp().run
}
