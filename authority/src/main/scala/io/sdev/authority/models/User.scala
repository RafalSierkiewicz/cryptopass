package io.sdev.authority.models

import io.sdev.authority.common._
import doobie.util.{Put, Get}

final case class User()

object User extends DbEntity {
  given dbGet: Get[Id] = Get[Int].tmap(Id(_))
  given dbPut: Put[Id] = Put[Int].tcontramap(id => id.value)
}
