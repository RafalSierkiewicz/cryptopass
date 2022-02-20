package io.sdev.authority.services

import cats.data._
import cats.implicits._
import io.circe.syntax._
import io.circe.parser._
import scala.util.control.NoStackTrace
import java.time.Instant
import org.http4s.Header.Raw
import org.bouncycastle.util.encoders.Hex
import io.sdev.authority.models.user.{DomainUser, domainUserEncoder, domainUserDecoder}
import scala.util.Try
import io.sdev.authority.configs.SecurityConfig
import io.sdev.authority.models.UserEntity
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit
import pdi.jwt._
import cats.Monad
import cats.effect.kernel.Sync

class TokenProvider[F[_]: Sync](config: SecurityConfig) {
  import TokenProvider._
  private val tokenDuration: FiniteDuration = FiniteDuration(1, TimeUnit.DAYS)

  def encode(user: UserEntity): F[JwtToken] = {
    Sync[F]
      .blocking {
        Jwt.encode(claimFor(user), config.secret, JwtAlgorithm.HS256)
      }
      .map(tokenStr => JwtToken(tokenStr))
  }

  def decode(tokenStr: String): EitherT[F, TokenErrors, DomainUser] = {
    for {
      tokenHeader <- getValidTokenHeader(tokenStr)
      claim <- decodeTokenHeader(tokenHeader)
      correctClaim <- EitherT.cond(isNonExpired(claim), claim, TokenExpired)
      tokenUser <- parseToken(correctClaim)
    } yield tokenUser
  }

  private def isNonExpired(claim: JwtClaim) = {
    claim.expiration
      .map(time => Instant.ofEpochMilli(time))
      .exists(exp => exp.isAfter(Instant.now))
  }

  private def claimFor(user: UserEntity): JwtClaim = {
    JwtClaim(
      content = DomainUser(user.id.value, user.username, user.email).asJson(domainUserEncoder).noSpaces,
      issuer = Some(config.issuer),
      issuedAt = Some(Instant.now.toEpochMilli),
      expiration = Some(Instant.now.toEpochMilli + tokenDuration.toMillis)
    )
  }

  private def getValidTokenHeader(token: String): EitherT[F, TokenErrors, String] =
    EitherT.cond(Jwt.isValid(token, config.secret, Seq(JwtAlgorithm.HS256)), token, InvalidToken)

  private def decodeTokenHeader(token: String): EitherT[F, TokenErrors, JwtClaim] =
    Either
      .fromTry(Jwt.decode(token, config.secret, Seq(JwtAlgorithm.HS256)))
      .toEitherT
      .leftMap(err => ParsingTokenError(err.getMessage))

  private def parseToken(claim: JwtClaim): EitherT[F, TokenErrors, DomainUser] = {
    for {
      json <- parse(claim.content).toEitherT.leftMap(_ => InvalidToken)
      domainUser <- json.as[DomainUser](domainUserDecoder).toEitherT.leftMap(err => ParsingTokenError(err.getMessage))
    } yield domainUser
  }
}

object TokenProvider {
  sealed trait TokenErrors extends NoStackTrace
  case object InvalidToken extends TokenErrors
  case object MissingHeader extends TokenErrors
  case object TokenExpired extends TokenErrors

  case class ParsingTokenError(msg: String) extends TokenErrors

  case class JwtToken(token: String)

}
