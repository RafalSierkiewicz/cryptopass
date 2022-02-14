package io.sdev.authority.daos

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.query._
import io.sdev.authority.models._

class UserDao {
  import UserDao._
  import User._

  def getAll: fs2.Stream[doobie.ConnectionIO, User] = getAllQ.stream

  def findById(id: Id): ConnectionIO[Option[User]] = findByIdQ(id).option

  def insert(title: String, body: String): doobie.ConnectionIO[Id] = {
    insertQ(title, body).withUniqueGeneratedKeys("id")
  }
}

object UserDao {
  implicit val han: LogHandler = LogHandler.jdkLogHandler
  def getAllQ: Query0[User] =
    sql"""SELECT id, title, body FROM articles""".query[User]

  def findByIdQ(id: User.Id): Query0[User] =
    sql"""SELECT id, title, body FROM articles WHERE id = ${id.value}""".query[User]

  def insertQ(title: String, body: String): Update0 =
    sql"""INSERT INTO articles(title,body) values ($title, $body)""".update
}
