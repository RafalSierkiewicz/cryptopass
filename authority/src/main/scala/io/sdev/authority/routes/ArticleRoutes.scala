package io.sdev.authority.routes

import cats.implicits._
import io.sdev.authority.services._
import io.sdev.authority.models._
import cats.effect.kernel._
import org.http4s._
import org.http4s.dsl.Http4sDsl

class UserRoutes[F[_]: Async](userService: UserService[F]) extends Http4sDsl[F] with Routes[F] {
  def routes: HttpRoutes[F] = authorizedGETRoutes

  private def authorizedGETRoutes: HttpRoutes[F] = {
    HttpRoutes.of { case GET -> Root / IntVar(id) =>
      Ok("hello")
    }
  }
}
