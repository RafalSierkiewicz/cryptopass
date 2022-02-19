package io.sdev.authority.services

import munit.CatsEffectSuite
import cats.effect.IO

class AuthServiceSuite extends CatsEffectSuite {
  val pepper = "pepper"
  test("password hashing") {
    val salt = AuthService.getSalt
    val password = "password"

    for {
      h1 <- AuthService.hash[IO](password, salt, pepper)
      _ <- AuthService.hash[IO](password, salt, pepper).assertEquals(h1)
    } yield ()
  }

  test("password validating") {
    val salt = AuthService.getSalt
    val password = "password"
    val h1 = AuthService.hash[IO](password, salt, pepper)
    for {
      hash <- h1
      _ <- AuthService.isPasswordValid[IO](hash, password, salt, pepper).assertEquals(true)
    } yield ()
  }
}
