package io.sdev.blog.daos
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.query._
import io.sdev.blog.models._
class ArticleDao {
  import ArticleDao._
  import Article._

  def getAll: fs2.Stream[doobie.ConnectionIO, Article] = getAllQ.stream

  def findById(id: Id): ConnectionIO[Option[Article]] = findByIdQ(id).option

  def insert(title: String, body: String): doobie.ConnectionIO[Id] = {
    insertQ(title, body).withUniqueGeneratedKeys("id")
  }
}

object ArticleDao {
  implicit val han: LogHandler = LogHandler.jdkLogHandler
  def getAllQ: Query0[Article] =
    sql"""SELECT id, title, body FROM articles""".query[Article]

  def findByIdQ(id: Article.Id): Query0[Article] =
    sql"""SELECT id, title, body FROM articles WHERE id = ${id.value}""".query[Article]

  def insertQ(title: String, body: String): Update0 =
    sql"""INSERT INTO articles(title,body) values ($title, $body)""".update
}
