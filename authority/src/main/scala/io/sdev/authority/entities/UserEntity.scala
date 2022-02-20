package io.sdev.authority.models

import doobie.util.{Put, Get}
import io.sdev.common.DbEntity
import io.sdev.authority.models.user.DomainUser

final case class UserEntity(id: UserEntity.Id, username: String, email: String, password: String, salt: String) {
  val domainUser = DomainUser(id.value, username, email)
}

object UserEntity extends DbEntity {
  given dbGet: Get[Id] = Get[Int].tmap(Id(_))
  given dbPut: Put[Id] = Put[Int].tcontramap(id => id.value)
}
