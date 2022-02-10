package io.sdev.blog.services

import io.sdev.blog.models._
import cats.Applicative
import cats.implicits._

trait HomeSerivce[F[_]] {
  def hello(n: Home.Name): F[Home.Greeting]
}

object HomeSerivce {
  given apply[F[_]](using ev: HomeSerivce[F]): HomeSerivce[F] = ev

  def impl[F[_]: Applicative]: HomeSerivce[F] = new HomeSerivce[F] {
    def hello(name: Home.Name): F[Home.Greeting] = Home.Greeting(s"Hello, $name").pure[F]
  }
}
