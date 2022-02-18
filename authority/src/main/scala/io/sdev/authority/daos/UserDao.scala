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

  def findByEmail(email: String): ConnectionIO[Option[UserEntity]] = findByEmailQ(email).option

  def insert(username: String, password: String, salt: String, email: String): doobie.ConnectionIO[Id] = {
    insertQ(username, password, salt, email).withUniqueGeneratedKeys("id")
  }
}

object UserDao {
  def getAllQ: Query0[UserEntity] =
    sql"""SELECT id, username, email, password, salt FROM articles""".query[UserEntity]

  def findByIdQ(id: UserEntity.Id): Query0[UserEntity] =
    sql"""SELECT id, username, email, password, salt FROM users WHERE id = ${id.value}""".query[UserEntity]

  def findByEmailQ(email: String): Query0[UserEntity] =
    sql"""SELECT id, username, email, password, salt FROM users WHERE id = $email""".query[UserEntity]

  def insertQ(username: String, password: String, salt: String, email: String): Update0 =
    sql"""INSERT INTO users(username,password,salt,email) values ($username, $password, $salt, $email)""".update
}
