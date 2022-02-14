package io.sdev.blog.app

import io.sdev.blog.routes._
import io.sdev.blog.services._
import cats.effect.kernel.Sync
import doobie.util.transactor.Transactor
import io.sdev.blog.daos.ArticleDao
import cats.effect.kernel.Async

trait Module[F[_]] {
  val homeRoutes: Routes[F]
  val articleRoutes: Routes[F]
}

class BlogModule[F[_]: Async](transactor: Transactor[F]) extends Module[F] {

  val homeService: HomeService[F] = HomeService[F]()
  val homeRoutes: HomeRoutes[F] = HomeRoutes[F](homeService)

  val articleDao: ArticleDao = ArticleDao()
  val articleService: ArticleService[F] = ArticleService[F](articleDao, transactor)
  val articleRoutes: ArticleRoutes[F] = ArticleRoutes[F](articleService)
}

object BlogModule {
  def make[F[_]: Async](transactor: Transactor[F]) = new BlogModule(transactor)
}
