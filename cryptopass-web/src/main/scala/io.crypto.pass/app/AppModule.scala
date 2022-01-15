package io.crypto.pass.app

import io.crypto.pass.controllers._

class AppModule extends Module {
  override val passwordController: PasswordController = new PasswordController()
}

object AppModule {
  def make = new AppModule()
}
