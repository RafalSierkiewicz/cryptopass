package io.sdev.blog.models

import io.circe._
import org.http4s._
import org.http4s.circe._

object Home {

  opaque type Name = String
  opaque type Greeting = String

  object Name:
    def apply(s: String): Name = s

  object Greeting:
    def apply(s: String): Greeting = s
    given Encoder[Greeting] = (greet: Greeting) => Json.obj(("message", Json.fromString(greet)))
    given greetingEntityEncoder[F[_]]: EntityEncoder[F, Greeting] = jsonEncoderOf[F, Greeting]

}
