package io.sdev.authority.app

import io.sdev.authority.routes._
import io.sdev.authority.services._
import cats.effect.kernel.Sync
import doobie.util.transactor.Transactor
import io.sdev.authority.daos.UserDao
import cats.effect.kernel.Async
import io.sdev.authority.configs.AuthorityConfig

trait Module[F[_]] {
  val userRoutes: Routes[F]
}

class AuthorityModule[F[_]: Async](transactor: Transactor[F], config: AuthorityConfig) extends Module[F] {

  lazy val userDao: UserDao = UserDao()
  lazy val userService: UserService[F] = UserService[F](userDao, config.security, transactor)
  lazy val authService: AuthService[F] = AuthService[F](userService, config.security)

  val userRoutes: UserRoutes[F] = UserRoutes[F](userService, authService)
}

object AuthorityModule {
  def make[F[_]: Async](transactor: Transactor[F], config: AuthorityConfig) = new AuthorityModule(transactor, config)
}
