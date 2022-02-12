package io.sdev.blog.daos
import doobie._
import doobie.implicits._
import doobie.util.query._
import io.sdev.blog.models._

class ArticleDao {
  import ArticleDao._
  import Article._

  def getAll: fs2.Stream[doobie.ConnectionIO, Article] = getAllQ.stream

  def findById(id: Id): ConnectionIO[Option[Article]] = findByIdQ(id).option

  def insert(title: String, body: String): fs2.Stream[doobie.ConnectionIO, Id] =
    insertQ(title, body).withGeneratedKeys[Id]("id")
}

object ArticleDao {
  def getAllQ: Query0[Article] =
    fr"""SELECT id, title, body FROM articles""".query[Article]

  def findByIdQ(id: Article.Id): Query0[Article] =
    fr"""SELECT id, title, body FROM articles WHERE id = $id""".query[Article]

  def insertQ(title: String, body: String): Update0 =
    fr"""INSERT INTO articles(title,body) values ($title, $body)""".update
}
