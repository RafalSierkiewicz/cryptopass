package io.sdev.authority.models

import io.circe.Encoder
import io.circe.Decoder

package object user {
  val domainUserEncoder: Encoder[DomainUser] =
    Encoder.forProduct3("id", "email", "username")(u => (u.id, u.email, u.username))
  val domainUserDecoder: Decoder[DomainUser] = Decoder.forProduct3("id", "username", "email")(DomainUser(_, _, _))
}
