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
  val expiredTokenStr =
    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpby5zZGV2IiwiZXhwIjoxNjQ1MzQ3NjU3NzU2LCJpYXQiOjE2NDUzNDc1OTc3NTYsImlkIjoxLCJlbWFpbCI6ImVtYWlsIiwidXNlcm5hbWUiOiJ1c2VybmFtZSJ9.csmFA5o1uipC2PS3FKzBddYJxJE2WuEr-M4DXKTk5F4"

  test("should issue token without errors") {
    provider.encode(mockedUser).map(_.token.isEmpty).assertEquals(false)
  }

  test("should fail on expired token") {
    import TokenProvider._
    provider.decode(expiredTokenStr).value.assertEquals(Left(TokenExpired))
  }

  test("should encode and decode token without errors") {
    val decoded = for {
      token <- provider.encode(mockedUser)
      decodedUser <- provider.decode(token.token).value
    } yield decodedUser
    decoded.assertEquals(Right(mockedUser.domainUser))
  }

}
