package io.sdev.authority.routes

import cats.implicits._
import io.sdev.authority.services._
import io.sdev.authority.models._
import cats.effect.kernel._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import io.sdev.authority.models.user._
import io.sdev.common.decoders._
class UserRoutes[F[_]: Async](userService: UserService[F]) extends Http4sDsl[F] with Routes[F] {
  given EntityDecoder[F, UserCreate] = protoDecoder[F, UserCreate]
  given EntityDecoder[F, AuthorizeUser] = protoDecoder[F, AuthorizeUser]

  val routes: HttpRoutes[F] = openPOSTRoutes

  private val openPOSTRoutes: HttpRoutes[F] = {
    HttpRoutes.of {
      case req @ POST -> Root / "authorize" =>
        Ok(for {
          authorizeData <- req.as[AuthorizeUser]
        } yield authorizeData.toByteArray)

      case req @ POST -> Root =>
        Ok(for {
          userToCreate <- req.as[UserCreate]
          userId <- userService.insert(userToCreate.username, userToCreate.email, userToCreate.password)
        } yield UserId(userId.value).toByteArray)
    }
  }
}
