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
import cats.syntax.validated
import org.http4s.Header.Raw
import io.circe.Parser
import java.util.Base64
import java.nio.charset.StandardCharsets
import org.bouncycastle.util.encoders.Hex
import io.sdev.authority.models.user._
import scala.util.Try

class AuthService[F[_]: Async](userService: UserService[F], config: SecurityConfig) {
  import AuthService._

  private val tokenDuration: FiniteDuration = FiniteDuration(10, TimeUnit.DAYS)
  private val tokenName = "X-AUTH-TOKEN"

  private val authUser: Kleisli[F, Request[F], Either[TokenErrors, DomainUser]] = Kleisli { request =>
    val authHeader = request.headers.get(CIString(tokenName)).toOptionT
    val userEither: EitherT[F, TokenErrors, DomainUser] = for {
      token <- authHeader.toRight(MissingHeader)
      tokenHeader <- getValidTokenHeader(token.head)
      tokenContent <- decodeTokenHeader(tokenHeader)
      tokenUser <- parseToken(tokenContent)
    } yield tokenUser
    userEither.value
  }

  val onAuthFailure: AuthedRoutes[TokenErrors, F] = Kleisli { reqContext =>
    reqContext.context match {
      case tokenErr: TokenErrors => OptionT.pure[F](Response[F](status = Status.Forbidden))
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
              content = Hex.toHexString(DomainUser(user.id.value, user.username, user.email).toByteArray),
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

  private def parseToken(claim: JwtClaim): EitherT[F, TokenErrors, DomainUser] =
    Try(Hex.decode(claim.content))
      .flatMap(bytes => Try(DomainUser.parseFrom(bytes)))
      .toEither
      .toEitherT[F]
      .leftMap[TokenErrors](err => ParsingTokenError(err.getMessage))
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

  def isPasswordValid[F[_]: Sync](dbPass: String, reqPass: String, dbSalt: String, configPepper: String): F[Boolean] = {
    hash(reqPass, dbSalt, configPepper).map(pass => dbPass == pass)
  }

  def getSalt: String = {
    val bytes: Array[Byte] = Array.ofDim(16)
    new SecureRandom().nextBytes(bytes)
    new String(Hex.encode(bytes))
  }

  def hash[F[_]: Sync](password: String, salt: String, pepper: String): F[String] = {
    Sync[F].blocking {
      val combined: String = s"$password$salt$pepper"
      val sha256 = SHA256.Digest()
      new String(Hex.encode(sha256.digest(combined.getBytes)))
    }
  }
}
