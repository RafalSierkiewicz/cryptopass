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
object BlogServer {

  def stream[F[_]: Async]: Stream[F, Nothing] = {
    for {
      client <- Stream.resource(EmberClientBuilder.default[F].build)
      httpApp = routes[F](client).orNotFound
      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      exitCode <- Stream.resource(
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build >>
          Resource.eval(Async[F].never)
      )
    } yield exitCode
  }.drain

  private def routes[F[_]: Async](client: Client[F]): HttpRoutes[F] = {
    BlogRoutes.helloWorldRoutes[F](HomeSerivce.impl[F])
  }
}
