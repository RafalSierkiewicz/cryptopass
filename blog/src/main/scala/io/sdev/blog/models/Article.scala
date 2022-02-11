package io.sdev.blog.models

import io.circe._
import io.circe.generic.semiauto._
import org.http4s._
import org.http4s.circe._

case class Article(title: String, body: String)

object Article {
  given Codec[Article] = deriveCodec[Article]
  given articleEntityEncoder[F[_]]: EntityEncoder[F, Article] = jsonEncoderOf[F, Article]
}
