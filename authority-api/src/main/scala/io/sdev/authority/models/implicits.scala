package io.sdev.authority.models

import io.circe.Encoder
import io.circe.Decoder
import org.http4s.EntityDecoder
import io.sdev.common.decoders._
import cats.effect.kernel.Async
import cats.implicits
import io.sdev.authority.models.user._

object implicits {
  given domainUserEncoder: Encoder[DomainUser] =
    Encoder.forProduct3("id", "email", "username")(u => (u.id, u.email, u.username))
  given domainUserDecoder: Decoder[DomainUser] = Decoder.forProduct3("id", "username", "email")(DomainUser(_, _, _))

  given authorizeDecoder[F[_]: Async]: EntityDecoder[F, AuthorizeUser] = protoDecoder[F, AuthorizeUser]
  given domainUserEntityDecoder[F[_]: Async]: EntityDecoder[F, DomainUser] = protoDecoder[F, DomainUser]
  given userCreateDecoder[F[_]: Async]: EntityDecoder[F, UserCreate] = protoDecoder[F, UserCreate]

}
