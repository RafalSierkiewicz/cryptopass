package io.sdev.blog.routes

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import io.sdev.blog.services._
import io.sdev.blog.models._

class HomeRoutes[F[_]: Sync](homeService: HomeService[F]) extends Http4sDsl[F] with Routes[F] {

  def routes: HttpRoutes[F] = authorizedRoutes

  private def authorizedRoutes: HttpRoutes[F] = {
    HttpRoutes.of { case GET -> Root / "hello" / name =>
      for {
        greeting <- homeService.hello(Home.Name(name))
        resp <- Ok(greeting)
      } yield resp
    }
  }

}
