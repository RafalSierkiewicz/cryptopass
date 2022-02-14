package io.sdev.authority.services

import io.sdev.authority.daos.UserDao
import doobie.util.transactor.Transactor
import doobie.implicits._
import cats.effect.kernel.Async
import io.sdev.authority.models._

class UserService[F[_]: Async](userDao: UserDao, xa: Transactor[F]) {

  def getAll: fs2.Stream[F, User] = userDao.getAll.transact(xa)

  def findById(id: User.Id): F[Option[User]] = userDao.findById(id).transact(xa)

  def insert(title: String, body: String): F[User.Id] = {
    userDao.insert(title, body).transact(xa)
  }

}
