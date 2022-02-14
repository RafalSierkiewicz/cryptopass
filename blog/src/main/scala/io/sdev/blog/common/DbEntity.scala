package io.sdev.blog.common
import io.circe.Encoder
import io.circe.syntax._
import doobie.util.{Get, Put}
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf
import io.circe.Json

trait DbEntity {
  opaque type Id = Int

  object Id {
    def apply(i: Int): Id = i
  }
  extension (id: Id) {
    def value: Int = id
  }

  given dbGet: Get[Id]
  given dbPut: Put[Id]
  given Encoder[Id] = Encoder.instance(id => Json.fromInt(id))
  given idEntityEncoder[F[_]]: EntityEncoder[F, Id] = jsonEncoderOf[F, Id]
}
