package io.crypto.pass.config

import zio.config._
import ConfigDescriptor._
import zio.config.typesafe.TypesafeConfigSource
import java.io.File

final case class CryptopassConfig(port: Int, db: DatabaseConfig)

final case class DatabaseConfig(url: String, user: String, password: String)

object CryptopassConfig {
  private val dbConfing: ConfigDescriptor[DatabaseConfig] = (string("url").zip(string("user")).zip(string("password"))).to[DatabaseConfig]
  private val descriptor: ConfigDescriptor[CryptopassConfig] = (int("port")).zip(nested("db")(dbConfing)).to[CryptopassConfig]

  val readLayer = read(CryptopassConfig.descriptor.from(
    TypesafeConfigSource.fromHoconFile(new File("config.conf"))
  )).toLayer
}
