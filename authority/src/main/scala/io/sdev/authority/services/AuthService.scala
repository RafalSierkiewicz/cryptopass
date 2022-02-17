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

case class InvalidUsernameOrPassword(email: String) extends NoStackTrace

case class JwtToken(token: String)
case class TokenUser(username: String, email: String)
class AuthService[F[_]: Async](userService: UserService[F], config: SecurityConfig) {
  import AuthService._
  given Codec[TokenUser] = deriveCodec[TokenUser]
  private val tokenDuration: FiniteDuration = FiniteDuration(10, TimeUnit.DAYS)
  private val tokenName = "x-auth"

  private val authUser: Kleisli[F, Request[F], Either[Error, DomainUser]] = Kleisli { request =>
    val authHeader = request.headers.get(CIString(tokenName))
    val tokenUserOpt = authHeader
      // head on nonemptylist
      .map(_.head)
      .filter(token => Jwt.isValid(token.value, config.secret, Seq(JwtAlgorithm.HS256)))
      .flatMap(validToken => Jwt.decode(validToken.value, config.secret, Seq(JwtAlgorithm.HS256)).toOption)
      .map(_.content)
      .flatMap(tokenContent => io.circe.parser.parse(tokenContent).flatMap(_.as[TokenUser]).toOption)

    val user = tokenUserOpt match {
      case Some(u) => userService.findByEmail(u.email)
      case None    => None.pure[F]
    }
    user.map(_.map(_.domainUser).toRight(new Error("")))
  }

  val onAuthFailure: AuthedRoutes[Error, F] = Kleisli { (req: AuthedRequest[F, Error]) =>
    // for any requests' auth failure we return 401
    req.req match {
      case _ =>
        OptionT.pure[F](Response[F](status = Status.Unauthorized))
    }
  }

  val middleware: AuthMiddleware[F, DomainUser] = AuthMiddleware(authUser, onAuthFailure)

  def login(email: String, password: String): F[JwtToken] = {
    userService.findByEmail(email).flatMap {
      case None       => InvalidUsernameOrPassword(email).raiseError[F, JwtToken]
      case Some(user) =>
        isPasswordValid(user.password, password, user.salt, config.pepper).flatMap {
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
}

object AuthService {

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
