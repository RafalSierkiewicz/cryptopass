package io.sdev.blog.app

import io.sdev.blog.routes._
import io.sdev.blog.services._
import cats.effect.kernel.Sync
import doobie.util.transactor.Transactor

trait Module[F[_]] {
  val homeRoutes: HomeRoutes[F]
}

class BlogModule[F[_]: Sync](transactor: Transactor[F]) extends Module[F] {

  val homeService: HomeService[F] = new HomeService[F]()
  val homeRoutes: HomeRoutes[F] = new HomeRoutes[F](homeService)
}

object BlogModule {
  def make[F[_]: Sync](transactor: Transactor[F]) = new BlogModule(transactor)
}
