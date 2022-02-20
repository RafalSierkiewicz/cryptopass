package io.sdev.blog.services

import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server._
import cats.data._
import cats.implicits._
import cats.effect.kernel.Async
import io.sdev.authority.client.AuthorityClient
import io.sdev.authority.models.user.DomainUser
import org.typelevel.ci.CIString
import scala.util.control.NoStackTrace

class AuthService[F[_]: Async](client: AuthorityClient[F]) {
  sealed trait AuthFailure extends NoStackTrace
  case object MissingHeader extends AuthFailure
  case object AuthFailure extends AuthFailure

  private val authUser: Kleisli[F, Request[F], Either[AuthFailure, DomainUser]] = Kleisli { request =>
    (for {
      token <- request.headers.get(CIString("X-AUTH-TOKEN")).toOptionT.toRight(MissingHeader)
      tokenStr = token.head.value
      domainUser <- EitherT.liftF(client.authorize(tokenStr)).leftMap(_ => AuthFailure)
    } yield domainUser).value
  }

  private val onAuthFailure: AuthedRoutes[AuthFailure, F] = Kleisli { reqError =>
    OptionT.pure[F](Response[F](status = Status.Forbidden))
  }

  val middleware: AuthMiddleware[F, DomainUser] = AuthMiddleware(authUser, onAuthFailure)

}
