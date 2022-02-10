package io.sdev.blog

import cats.Applicative
import cats.implicits._
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

trait HelloWorld[F[_]] {
  def hello(n: HelloWorld.Name): F[HelloWorld.Greeting]
}

object HelloWorld {
  given apply[F[_]](using ev: HelloWorld[F]): HelloWorld[F] = ev

  final case class Name(name: String)
  final case class Greeting(greeting: String)

  object Greeting {
    given Encoder[Greeting] = (greet: Greeting) => Json.obj(("message", Json.fromString(greet.greeting)))
    given greetingEntityEncoder[F[_]]: EntityEncoder[F, Greeting] = jsonEncoderOf[F, Greeting]
  }

  def impl[F[_]: Applicative]: HelloWorld[F] = new HelloWorld[F] {
    def hello(n: HelloWorld.Name): F[HelloWorld.Greeting] = Greeting("Hello, " + n.name).pure[F]
  }
}
