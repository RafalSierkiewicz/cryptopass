package io.sdev.blog.services

import io.sdev.blog.models._
import cats.Applicative
import cats.implicits._

class HomeService[F[_]: Applicative] {
  def hello(name: Home.Name): F[Home.Greeting] = Home.Greeting(s"Hello, $name").pure[F]
}
