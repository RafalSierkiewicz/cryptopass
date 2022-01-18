package io.crypto.pass.config

import zio.config._
import ConfigDescriptor._
import zio.config.typesafe.TypesafeConfigSource
import java.io.File

object AppConfig {
  import DatabaseConfig._

  final case class Config(port: Int, db: DatabaseConfig.Config)

  private val descriptor: ConfigDescriptor[Config] = (int("port")).zip(nested("db")(dbConfigDescriptor)).to[Config]

  val readLayer = read(descriptor.from(
    TypesafeConfigSource.fromHoconFile(new File("config.conf"))
  )).toLayer
}
