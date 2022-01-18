package io.crypto.pass.models

import zio.json._

final case class ContractUser(address: String, masterPassword: String, isAllowed: Boolean)

object ContractUser {
    given JsonCodec[ContractUser] = DeriveJsonCodec.gen[ContractUser]
}
