package io.crypto.pass.app

import io.crypto.pass.controllers._
trait Module {
  def passwordController: PasswordEntityController
}
