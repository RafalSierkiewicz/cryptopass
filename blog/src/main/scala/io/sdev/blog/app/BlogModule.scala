package io.sdev.blog.app

import io.sdev.blog.routes._
import io.sdev.blog.services._
import cats.effect.kernel.Sync
import doobie.util.transactor.Transactor
import io.sdev.blog.daos.ArticleDao
import cats.effect.kernel.Async
import org.http4s.client.Client
import io.sdev.authority.client.AuthorityClient

trait Module[F[_]] {
  val homeRoutes: Routes[F]
  val articleRoutes: Routes[F]
}

class BlogModule[F[_]: Async](transactor: Transactor[F], client: Client[F]) extends Module[F] {

  val authorityClient: AuthorityClient[F] = AuthorityClient[F](client)

  val authService: AuthService[F] = AuthService[F](authorityClient)
  val homeService: HomeService[F] = HomeService[F]()
  val homeRoutes: HomeRoutes[F] = HomeRoutes[F](homeService)

  val articleDao: ArticleDao = ArticleDao()
  val articleService: ArticleService[F] = ArticleService[F](articleDao, transactor)
  val articleRoutes: ArticleRoutes[F] = ArticleRoutes[F](articleService, authService)
}

object BlogModule {
  def make[F[_]: Async](transactor: Transactor[F], client: Client[F]) = new BlogModule(transactor, client)
}
