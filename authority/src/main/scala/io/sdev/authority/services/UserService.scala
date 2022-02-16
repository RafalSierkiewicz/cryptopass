package io.sdev.authority.services

import io.sdev.authority.daos.UserDao
import io.sdev.authority.configs.SecurityConfig
import doobie.util.transactor.Transactor
import doobie.implicits._
import cats.effect.kernel.Async
import cats.implicits._
import io.sdev.authority.models._
import java.security.SecureRandom
import org.bouncycastle.jcajce.provider.digest.SHA256
import cats.effect.kernel.Sync

class UserService[F[_]: Async](userDao: UserDao, securityConfig: SecurityConfig, xa: Transactor[F]) {

  def getAll: fs2.Stream[F, UserEntity] = userDao.getAll.transact(xa)

  def findById(id: UserEntity.Id): F[Option[UserEntity]] = userDao.findById(id).transact(xa)

  def findByEmail(email: String): F[Option[UserEntity]] = userDao.findByEmail(email).transact(xa)

  def authorize(email: String, password: String) = {}

  def insert(username: String, email: String, password: String): F[UserEntity.Id] = {
    for {
      salt <- Sync[F].pure(AuthService.getSalt)
      hashed <- AuthService.hash(password, salt, securityConfig.pepper)
      id <- userDao.insert(username, hashed, salt, email).transact(xa)
    } yield id
  }

}
