package io.crypto.pass.services

import io.crypto.pass.contracts.PasswordManager
import io.crypto.pass.models.PasswordEntity
import scala.jdk.CollectionConverters._
import zio._
class PasswordEntityService(manager: PasswordManager) {

  def store(masterPassword: String, entity: PasswordEntity): IO[Throwable, Boolean] = {
    IO.effect {
      manager.store(masterPassword, entity.title, aes(masterPassword, entity.password), aes(masterPassword, entity.password)).send.isStatusOK
    }
  }

  def getAll(masterPassword: String): IO[Throwable, Vector[PasswordEntity]] = {
    IO.effect {
      manager.getAll(masterPassword).send.asScala.toVector.map { e => e.asInstanceOf[PasswordEntity] }
    }
  }

  private def aes(key: String, plainText: String) = ""
}
