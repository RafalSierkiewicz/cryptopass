package io.sdev.blog.common
import io.circe.Encoder
import io.circe.syntax._
import doobie.util.{Get, Put}
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

trait DbEntity {
  opaque type Id = Int

  object Id:
    def apply(i: Int): Id = i

  given Get[Id] = Get[Int]
  given Put[Id] = Put[Int]
  given Encoder[Id] = Encoder.instance(id => id.asJson)
  given idEntityEncoder[F[_]]: EntityEncoder[F, Id] = jsonEncoderOf[F, Id]
}
