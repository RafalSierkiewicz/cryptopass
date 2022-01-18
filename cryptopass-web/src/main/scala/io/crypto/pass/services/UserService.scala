package io.crypto.pass.services

import io.crypto.pass.contracts.PasswordManager
import zio._
import io.crypto.pass.models.blockchain.Address._

class UserService(manager: PasswordManager) {
  def create(address: Address, password: String) = {
    IO.effect(manager.registerUser(address.solidityAddress, password, true).send)
  }

  def unAllow(address: Address) = {
    IO.effect(manager.unregisterUser(address.solidityAddress, false))
  }
}
