package io.crypto.pass.app

import io.crypto.pass.controllers._

class AppModule extends Module {
  override val passwordController: PasswordEntityController = new PasswordEntityController()
}

object AppModule {
  def make = new AppModule()
}
