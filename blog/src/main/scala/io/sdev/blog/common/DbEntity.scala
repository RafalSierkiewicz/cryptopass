package io.sdev.blog.common
import io.circe.Encoder
import io.circe.syntax._
import doobie.util.{Get, Put}

trait DbEntity {
  opaque type Id = Int

  given Get[Id] = Get[Int]
  given Put[Id] = Put[Int]
  given Encoder[Id] = Encoder.instance(id => id.asJson)
}
