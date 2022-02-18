package io.sdev.authority.services

import cats._, cats.effect._, cats.implicits._, cats.data._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server._
import pdi.jwt._
import org.bouncycastle.jcajce.provider.digest.SHA256
import java.security.SecureRandom

import io.sdev.authority.models.UserEntity
import io.sdev.authority.services.UserService
import io.sdev.authority.configs.SecurityConfig

import scala.util.control.NoStackTrace
import io.circe.Codec
import io.circe.generic.semiauto._
import io.circe.syntax._
import java.time.Instant
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit
import org.typelevel.ci.CIString
import io.sdev.authority.models.DomainUser
import cats.syntax.validated
import org.http4s.Header.Raw
import io.circe.Parser

class AuthService[F[_]: Async](userService: UserService[F], config: SecurityConfig) {
  import AuthService._

  given Codec[TokenUser] = deriveCodec[TokenUser]
  private val tokenDuration: FiniteDuration = FiniteDuration(10, TimeUnit.DAYS)
  private val tokenName = "X-AUTH-TOKEN"

  private val authUser: Kleisli[F, Request[F], Either[TokenErrors | AuthorizeErrors, DomainUser]] = Kleisli { request =>
    val authHeader = request.headers.get(CIString(tokenName)).toOptionT
    val userEither: EitherT[F, TokenErrors | AuthorizeErrors, UserEntity] = for {
      token <- authHeader.toRight(MissingHeader)
      tokenHeader <- getValidTokenHeader(token.head)
      tokenContent <- decodeTokenHeader(tokenHeader)
      tokenUser <- parseToken(tokenContent)
      user <- OptionT(userService.findByEmail(tokenUser.email)).toRightF(UserMissing.pure[F])
    } yield user
    userEither.map(_.domainUser).value
  }

  val onAuthFailure: AuthedRoutes[TokenErrors | AuthorizeErrors, F] = Kleisli { reqContext =>
    reqContext.context match {
      case tokenErr: TokenErrors    => OptionT.pure[F](Response[F](status = Status.Forbidden))
      case authErr: AuthorizeErrors => OptionT.pure[F](Response[F](status = Status.Unauthorized))
    }
  }

  val middleware: AuthMiddleware[F, DomainUser] = AuthMiddleware(authUser, onAuthFailure)

  def login(email: String, password: String): F[JwtToken] = {
    userService.findByEmail(email).flatMap {
      case None       => InvalidUsernameOrPassword(email).raiseError[F, JwtToken]
      case Some(user) =>
        AuthService.isPasswordValid(user.password, password, user.salt, config.pepper).flatMap {
          case false => InvalidUsernameOrPassword(email).raiseError[F, JwtToken]
          case true  =>
            val claim = JwtClaim(
              content = TokenUser(user.username, user.email).asJson.noSpaces,
              issuer = Some(config.issuer),
              issuedAt = Some(Instant.now.toEpochMilli),
              expiration = Some(Instant.now.toEpochMilli + tokenDuration.toMillis)
            )

            for {
              token <- Sync[F].delay(Jwt.encode(claim, config.secret, JwtAlgorithm.HS256))
            } yield JwtToken(token)

        }
    }
  }

  private def getValidTokenHeader(token: Raw): EitherT[F, TokenErrors, String] =
    EitherT.cond(Jwt.isValid(token.value, config.secret, Seq(JwtAlgorithm.HS256)), token.value, InvalidToken)

  private def decodeTokenHeader(token: String): EitherT[F, TokenErrors, JwtClaim] =
    Either
      .fromTry(Jwt.decode(token, config.secret, Seq(JwtAlgorithm.HS256)))
      .toEitherT
      .leftMap(err => ParsingTokenError(err.getMessage))

  private def parseToken(claim: JwtClaim): EitherT[F, TokenErrors, TokenUser] =
    io.circe.parser
      .parse(claim.content)
      .flatMap(_.as[TokenUser])
      .toEitherT
      .leftMap(parsingErr => ParsingTokenError(parsingErr.getMessage))
}

object AuthService {
  case class InvalidUsernameOrPassword(email: String) extends NoStackTrace

  sealed trait AuthorizeErrors extends NoStackTrace
  case object UserMissing extends AuthorizeErrors

  sealed trait TokenErrors extends NoStackTrace
  case object InvalidToken extends TokenErrors
  case object MissingHeader extends TokenErrors
  case class ParsingTokenError(msg: String) extends TokenErrors

  case class JwtToken(token: String)
  case class TokenUser(username: String, email: String)

  def isPasswordValid[F[_]: Sync](dbPass: String, reqPass: String, dbSalt: String, configPepper: String): F[Boolean] = {
    hash(reqPass, dbSalt, configPepper).map(pass => dbPass == pass)
  }

  def getSalt: String = {
    val bytes: Array[Byte] = Array.ofDim(16)
    new SecureRandom().nextBytes(bytes)
    new String(bytes)
  }

  def hash[F[_]: Sync](password: String, salt: String, pepper: String): F[String] = {
    Sync[F].delay {
      val combined: String = s"$password$salt$pepper"
      val sha256 = new SHA256.Digest()
      new String(sha256.digest(combined.getBytes))
    }
  }
}
