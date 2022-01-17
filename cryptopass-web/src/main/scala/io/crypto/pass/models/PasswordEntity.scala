package io.crypto.pass.models

import zio.json.DeriveJsonCodec

import zio.json._

final case class PasswordEntity(title: String, username: String, password: String)

object PasswordEntity {
  given JsonCodec[PasswordEntity] = DeriveJsonCodec.gen[PasswordEntity]
}
