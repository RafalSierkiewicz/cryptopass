package io.sdev.authority.routes

import cats.implicits._
import io.sdev.authority.services._
import io.sdev.authority.models._
import cats.effect.kernel._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import io.sdev.authority.models.user.User

class UserRoutes[F[_]: Async](userService: UserService[F]) extends Http4sDsl[F] with Routes[F] {
  def routes: HttpRoutes[F] = openGETRoutes
  private def openGETRoutes: HttpRoutes[F] = {
    HttpRoutes.of { case GET -> Root / email =>
      if (email == "") {

        Ok(User("elo", "test@email.com", "password").toByteArray)
      } else {
        NotFound()
      }
    }
  }
}
