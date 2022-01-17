package io.crypto.pass.models

import zio.json._

final case class User(address: String, masterPassword: String, isAllowed: Boolean)

object User {
    given JsonCodec[User] = DeriveJsonCodec.gen[User]
}
