package io.crypto.pass.config

import zio.config._
import ConfigDescriptor._

object DatabaseConfig {
  final case class Config(url: String, user: String, password: String)

  val dbConfigDescriptor: ConfigDescriptor[Config] = (string("url").zip(string("user")).zip(string("password"))).to[Config]

}
