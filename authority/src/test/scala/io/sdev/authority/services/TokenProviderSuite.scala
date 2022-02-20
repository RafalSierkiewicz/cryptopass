package io.sdev.authority.services

import cats.effect._
import cats.implicits._
import io.sdev.authority.configs.SecurityConfig
import io.sdev.authority.services.TokenProvider
import io.sdev.authority.models.UserEntity

class TokenProviderSuite extends munit.CatsEffectSuite {
  val securityConfig = SecurityConfig("pepper", "secret", "io.sdev")
  val provider = new TokenProvider[IO](securityConfig)
  val mockedUser = UserEntity(UserEntity.Id(1), "username", "email", "password", "salt")
  val tokenStr =
    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpby5zZGV2IiwiZXhwIjoxNjQ2MjA5Nzk3Mzc5LCJpYXQiOjE2NDUzNDU3OTczNzksImlkIjoxLCJlbWFpbCI6ImVtYWlsIiwidXNlcm5hbWUiOiJ1c2VybmFtZSJ9.RUk4HwjSO_1yBHebDm3B2-C4YYMe_tuV5091BKGO3Kw"

  test("should issue token without errors") {
    provider.encode(mockedUser).map(_.token.isEmpty).assertEquals(false)
  }

  test("should decode token without errors") {
    provider.decode(tokenStr).value.map(_.isRight).assertEquals(true)
  }

  test("should encode and decode token without errors") {
    val decoded = for {
      token <- provider.encode(mockedUser)
      decodedUser <- provider.decode(token.token).value
    } yield decodedUser
    decoded.assertEquals(Right(mockedUser.domainUser))
  }

}
