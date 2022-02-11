package io.sdev.blog

import cats.effect.{Async, Resource}
import cats.syntax.all._
import com.comcast.ip4s._
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import cats.effect.kernel.Sync
import org.http4s.client.Client
import cats.effect.Concurrent
import org.http4s.HttpRoutes
import io.sdev.blog.services.HomeSerivce
import io.sdev.blog.routes._
import io.sdev.blog.configs.BlogConfig
import pureconfig._
object BlogServer {

  def stream[F[_]: Async]: Stream[F, Nothing] = {
    for {
      client <- Stream.resource(EmberClientBuilder.default[F].build)
      httpApp = routes[F](client).orNotFound
      conf <- Stream.eval(config)
      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      exitCode <- Stream.resource(
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port = Port.fromInt(conf.app.port).get)
          .withHttpApp(finalHttpApp)
          .build >>
          Resource.eval(Async[F].never)
      )
    } yield exitCode
  }.drain

  def config[F[_]: Sync]: F[BlogConfig] = {
    Sync[F].delay(ConfigSource.resources("blog.conf").loadOrThrow[BlogConfig])
  }
  private def routes[F[_]: Async](client: Client[F]): HttpRoutes[F] = {
    BlogRoutes.helloWorldRoutes[F](HomeSerivce.impl[F])
  }
}
