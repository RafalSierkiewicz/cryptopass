package io.sdev.blog.configs
import pureconfig._
import pureconfig.error.ConfigReaderFailures
import cats.implicits._

final case class BlogConfig(db: DbConfig, app: AppConfig)

final case class DbConfig(user: String, password: String, url: String)
final case class AppConfig(port: Int)
object BlogConfig {
  private given ConfigReader[DbConfig] = ConfigReader.forProduct3("user", "password", "url")(DbConfig(_, _, _))
  private given ConfigReader[AppConfig] = ConfigReader.forProduct1("port")(AppConfig(_))

  given ConfigReader[BlogConfig] = ConfigReader.fromCursor { cursor =>
    for {
      objCur <- cursor.asObjectCursor
      blogCur <- objCur.atKey("blog")
      blogObjCur <- blogCur.asObjectCursor
      dbConfig <- read[DbConfig](blogObjCur, "db")
      appConfig <- read[AppConfig](blogObjCur, "app")
    } yield BlogConfig(dbConfig, appConfig)
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
