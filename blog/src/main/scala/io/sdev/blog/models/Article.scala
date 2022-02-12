package io.sdev.blog.models

import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
import org.http4s._
import org.http4s.circe._
import io.sdev.blog.common._
import doobie.util._

case class Article(id: Article.Id, title: String, body: String)

object Article extends DbEntity {
  given Encoder[Article] = deriveEncoder[Article]
  given articleEntityEncoder[F[_]]: EntityEncoder[F, Article] = jsonEncoderOf[F, Article]
}
