package io.sdev.blog.routes

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import io.sdev.blog.services.HomeSerivce
import io.sdev.blog.models._

object BlogRoutes {

  def helloWorldRoutes[F[_]: Sync](H: HomeSerivce[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] { case GET -> Root / "hello" / name =>
      for {
        greeting <- H.hello(Home.Name(name))
        resp <- Ok(greeting)
      } yield resp
    }
  }
}
