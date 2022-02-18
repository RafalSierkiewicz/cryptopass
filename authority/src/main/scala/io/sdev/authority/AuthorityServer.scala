package io.sdev.authority

import cats.effect.{Async, Resource}
import cats.syntax.all._
import com.comcast.ip4s._
import doobie._
import doobie.implicits._
import fs2.Stream
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import cats.effect.kernel.Async
import org.http4s.client.Client
import org.http4s.HttpRoutes
import io.sdev.authority.routes._
import pureconfig._
import io.sdev.authority.configs._
import io.sdev.authority.app._
import org.http4s.server.Router
import org.flywaydb.core.Flyway
import doobie.hikari.HikariTransactor
import cats.effect.kernel.Sync
import cats.effect.IO
import cats.effect.unsafe.IORuntime
import io.sdev.authority.configs.AppConfig
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration
import doobie.util.transactor

object AuthorityServer {

  def stream[F[_]: Async]: Stream[F, Nothing] = {
    for {
      conf <- Stream.eval[F, AuthorityConfig](config)
      transactor <- Stream.resource[F, Transactor[F]](createTransactor(conf.db))
      httpApp = routes[F](AuthorityModule.make(transactor, conf)).orNotFound
      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      _ <- Stream.eval(runMigrations(conf.db))
      exitCode <- Stream.resource(
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port = Port.fromInt(conf.app.port).get)
          .withHttpApp(finalHttpApp)
          .build >>
          Resource.eval(Async[F].never)
      )
    } yield exitCode
  }.drain

  private def runMigrations[F[_]: Sync](dbConfig: DbConfig): F[Boolean] =
    for {
      migrations <- Sync[F].blocking(
        Flyway
          .configure(getClass.getClassLoader)
          .dataSource(dbConfig.url, dbConfig.user, dbConfig.password)
          .load
      )
      status <- Sync[F].blocking(migrations.migrate).map(_.success)
    } yield status

  private def config[F[_]: Sync]: F[AuthorityConfig] =
    Sync[F].blocking(ConfigSource.resources("authority.conf").loadOrThrow[AuthorityConfig])

  private def routes[F[_]: Async](module: Module[F]): HttpRoutes[F] =
    Router("/users" -> module.userRoutes.routes)

  private def createTransactor[F[_]: Async](dbConfig: DbConfig): Resource[F, Transactor[F]] = {
    for {
      ec <- ExecutionContexts.fixedThreadPool(32)
      transactor <- HikariTransactor
        .newHikariTransactor[F]("org.postgresql.Driver", dbConfig.url, dbConfig.user, dbConfig.password, ec)
    } yield transactor

  }

}
