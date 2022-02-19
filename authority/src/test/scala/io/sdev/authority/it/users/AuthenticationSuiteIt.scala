package io.sdev.authority.it.users

import munit.CatsEffectSuite
import io.sdev.authority.it._
import io.sdev.authority.daos.UserDao
import io.sdev.authority.services.UserService
import io.sdev.authority.configs.SecurityConfig
import cats.effect.IO
import cats.implicits._
import cats.effect.kernel.Resource
import io.sdev.authority.routes.UserRoutes
import io.sdev.authority.services.AuthService
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import io.sdev.authority.models.user.UserCreate
import io.sdev.authority.models.user.UserId
import io.sdev.common.decoders._
import doobie.util.transactor.Transactor
import io.sdev.authority.models.user.AuthorizeUser

class AuthenticationSuiteIt extends DatabaseMock {
  val userDao = UserDao()
  val securityConfig = SecurityConfig("pepper", "secret", "io.sdev")
  given EntityDecoder[IO, UserId] = protoDecoder[IO, UserId]

  test("user registration test") {
    val service = new UserService[IO](userDao, securityConfig, xa)
    val authService = new AuthService[IO](service, securityConfig)
    val routes = new UserRoutes(service, authService).routes.orNotFound
    val request =
      Request[IO](Method.POST, uri"/")
        .withEntity(UserCreate("uname1", "email1", "password1").toByteArray)
    val res = routes.run(request)

    val checkF = (userIdBody: IO[UserId]) => userIdBody.map(_.id).assertEquals(1)
    check[UserId](res, Status.Ok)(checkF)
  }

  test("user login test") {
    val service = new UserService[IO](userDao, securityConfig, xa)
    val authService = new AuthService[IO](service, securityConfig)
    val routes = new UserRoutes(service, authService).routes.orNotFound
    val user = UserCreate("uname1", "email1", "password1")
    val request =
      Request[IO](Method.POST, uri"")
        .withEntity(user.toByteArray)
    val res = routes.run(request)
    val checkUser = (userIdBody: IO[UserId]) => userIdBody.map(_.id).assertEquals(1)
    check[UserId](res, Status.Ok)(checkUser)

    val loginRequest = Request[IO](Method.POST, uri"login")
      .withEntity(AuthorizeUser(user.email, user.password).toByteArray)
    val loginRes = routes.run(loginRequest)

    val checkToken = (token: IO[String]) => token.map(_.isEmpty).assertEquals(false)
    check[String](loginRes, Status.Ok)(checkToken)
  }

  def check[A](actual: IO[Response[IO]], expectedStatus: Status)(
    bodyCheck: IO[A] => IO[Unit]
  )(using EntityDecoder[IO, A]): IO[Unit] = {
    val response = actual.unsafeRunSync()
    for {
      _ <- assertEquals(response.status, expectedStatus).pure[IO]
      _ <- response.body.compile.toVector.map(_.isEmpty).assertEquals(false)
      _ <- bodyCheck(response.as[A])
    } yield ()
  }
}
