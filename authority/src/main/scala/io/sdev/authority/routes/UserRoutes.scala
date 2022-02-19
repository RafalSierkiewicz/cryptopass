package io.sdev.authority.routes

import cats.implicits._
import io.sdev.authority.services._
import io.sdev.authority.models._
import cats.effect.kernel.Async
import org.http4s._
import org.http4s.dsl.Http4sDsl
import io.sdev.authority.models.user._
import io.sdev.authority.models.user.DomainUser
import io.sdev.common.decoders._
class UserRoutes[F[_]: Async](userService: UserService[F], authService: AuthService[F])
    extends Http4sDsl[F]
    with Routes[F] {
  given EntityDecoder[F, UserCreate] = protoDecoder[F, UserCreate]
  given EntityDecoder[F, AuthorizeUser] = protoDecoder[F, AuthorizeUser]

  def routes: HttpRoutes[F] = openPOSTRoutes <+> authService.middleware(authorizedPostRoutes)

  private val openPOSTRoutes: HttpRoutes[F] = {
    HttpRoutes.of {
      case req @ POST -> Root / "login" =>
        Ok(for {
          authorizeData <- req.as[AuthorizeUser]
          token <- authService.login(authorizeData.email, authorizeData.password)
        } yield token.token)

      case req @ POST -> Root =>
        Ok(for {
          userToCreate <- req.as[UserCreate]
          userId <- userService.insert(userToCreate.username, userToCreate.email, userToCreate.password)
        } yield UserId(userId.value).toByteArray)
    }
  }

  private val authorizedPostRoutes: AuthedRoutes[DomainUser, F] = {
    AuthedRoutes.of { case req @ POST -> Root / "authorize" as user =>
      Ok(DomainUser(user.id, user.username, user.email).toByteArray.pure[F])
    }
  }
}
