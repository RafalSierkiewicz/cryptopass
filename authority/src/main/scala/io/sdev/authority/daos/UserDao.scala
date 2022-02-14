package io.sdev.authority.daos

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.query._
import io.sdev.authority.models._

class UserDao {
  import UserDao._
  import UserEntity._

  def getAll: fs2.Stream[doobie.ConnectionIO, UserEntity] = getAllQ.stream

  def findById(id: Id): ConnectionIO[Option[UserEntity]] = findByIdQ(id).option

  def insert(title: String, body: String): doobie.ConnectionIO[Id] = {
    insertQ(title, body).withUniqueGeneratedKeys("id")
  }
}

object UserDao {
  implicit val han: LogHandler = LogHandler.jdkLogHandler
  def getAllQ: Query0[UserEntity] =
    sql"""SELECT id, title, body FROM articles""".query[UserEntity]

  def findByIdQ(id: UserEntity.Id): Query0[UserEntity] =
    sql"""SELECT id, title, body FROM articles WHERE id = ${id.value}""".query[UserEntity]

  def insertQ(title: String, body: String): Update0 =
    sql"""INSERT INTO articles(title,body) values ($title, $body)""".update
}
