package io.sdev.blog.models

import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
import org.http4s._
import org.http4s.circe._
import doobie.util._
import _root_.io.sdev.common.DbEntity

case class Article(id: Article.Id, title: String, body: String)

object Article extends DbEntity {
  given dbGet: Get[Id] = Get[Int].tmap(Id(_))
  given dbPut: Put[Id] = Put[Int].tcontramap(id => id.value)

  given Encoder[Article] = deriveEncoder[Article]
  given articleEntityEncoder[F[_]]: EntityEncoder[F, Article] = jsonEncoderOf[F, Article]
}
