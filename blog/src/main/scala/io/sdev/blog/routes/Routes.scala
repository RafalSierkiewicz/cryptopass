package io.sdev.blog.routes

import org.http4s.HttpRoutes

trait Routes[F[_]] {
  def routes: HttpRoutes[F]
}
