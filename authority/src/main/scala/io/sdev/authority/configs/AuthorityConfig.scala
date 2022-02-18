package io.sdev.authority.configs

import pureconfig._
import pureconfig.error.ConfigReaderFailures
import cats.implicits._

final case class AuthorityConfig(db: DbConfig, app: AppConfig, security: SecurityConfig)

final case class DbConfig(user: String, password: String, url: String)
final case class AppConfig(port: Int)
final case class SecurityConfig(pepper: String, secret: String, issuer: String)
object AuthorityConfig {

  private given ConfigReader[DbConfig] = ConfigReader.forProduct3("user", "password", "url")(DbConfig(_, _, _))
  private given ConfigReader[AppConfig] = ConfigReader.forProduct1("port")(AppConfig(_))
  private given ConfigReader[SecurityConfig] =
    ConfigReader.forProduct3("pepper", "secret", "issuer")(SecurityConfig(_, _, _))

  given ConfigReader[AuthorityConfig] = ConfigReader.fromCursor { cursor =>
    for {
      objCur <- cursor.asObjectCursor
      authorityCur <- objCur.atKey("authority")
      authorityObjCur <- authorityCur.asObjectCursor
      dbConfig <- read[DbConfig](authorityObjCur, "db")
      appConfig <- read[AppConfig](authorityObjCur, "app")
      securityConfig <- read[SecurityConfig](authorityObjCur, "security")
    } yield AuthorityConfig(dbConfig, appConfig, securityConfig)
  }

  private def read[A](objCur: ConfigObjectCursor, key: String)(using
    reader: ConfigReader[A]
  ): Either[ConfigReaderFailures, A] = {
    for {
      cur <- objCur.atKey(key)
      config <- reader.from(cur)
    } yield config
  }
}
