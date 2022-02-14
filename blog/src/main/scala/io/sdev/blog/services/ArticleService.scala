package io.sdev.blog.services

import io.sdev.blog.daos.ArticleDao
import doobie.util.transactor.Transactor
import doobie.implicits._
import cats.effect.kernel.Async
import io.sdev.blog.models._
import cats.effect.kernel.Sync
import cats.Monad
import cats.effect.kernel.MonadCancelThrow

class ArticleService[F[_]: Async](articleDao: ArticleDao, xa: Transactor[F]) {

  def getAll: fs2.Stream[F, Article] = articleDao.getAll.transact(xa)

  def findById(id: Article.Id): F[Option[Article]] = articleDao.findById(id).transact(xa)

  def insert(title: String, body: String): fs2.Stream[F, Article.Id] =
    articleDao.insert(title, body).transact(xa)

}
