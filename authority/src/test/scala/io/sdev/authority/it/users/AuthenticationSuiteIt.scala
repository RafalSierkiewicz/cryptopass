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
import io.sdev.authority.models.user._
import io.sdev.authority.services.TokenProvider
import io.sdev.it.DatabaseMock

class AuthenticationSuiteIt extends DatabaseMock {
  val userDao = UserDao()
  val securityConfig = SecurityConfig("pepper", "secret", "io.sdev")

  given EntityDecoder[IO, UserId] = protoDecoder[IO, UserId]
  given EntityDecoder[IO, DomainUser] = protoDecoder[IO, DomainUser]

  lazy val service = new UserService[IO](userDao, securityConfig, xa)
  lazy val tokenProvider = new TokenProvider[IO](securityConfig)
  lazy val authService = new AuthService[IO](service, tokenProvider, securityConfig)
  lazy val routes = new UserRoutes(service, authService).routes.orNotFound

  test("user registration test") {
    val request =
      Request[IO](Method.POST, uri"/")
        .withEntity(UserCreate("uname1", "email1", "password1").toByteArray)
    val res = routes.run(request)

    val checkF = (userIdBody: IO[UserId]) => userIdBody.map(_.id).assertEquals(1)
    check[UserId](res, Status.Ok)(checkF)
  }

  test("user login test") {
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

  test("user failed login test") {
    val user = UserCreate("uname1", "email1", "password1")

    val request = Request[IO](Method.POST, uri"").withEntity(user.toByteArray)
    val res = routes.run(request)

    val checkUser = (userIdBody: IO[UserId]) => userIdBody.map(_.id).assertEquals(1)
    check[UserId](res, Status.Ok)(checkUser)

    val loginRequest =
      Request[IO](Method.POST, uri"login").withEntity(AuthorizeUser(user.email, "password2").toByteArray)
    val loginRes = routes.run(loginRequest)

    check[String](loginRes, Status.Forbidden)
  }

  test("user token veryfication") {
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

    val token = loginRes.unsafeRunSync().as[String].unsafeRunSync()
    val authRequest =
      Request[IO](Method.POST, uri"/authorize")
        .withHeaders(Headers(Header("X-AUTH-TOKEN", token)))

    val authRes = routes.run(authRequest)

    val checkDomainUser = (user: IO[DomainUser]) => user.assertEquals(DomainUser(1, "uname1", "email1"))
    check[DomainUser](authRes, Status.Ok)(checkDomainUser)
  }

  def check[A](actual: IO[Response[IO]], expectedStatus: Status)(
    bodyCheck: IO[A] => IO[Unit] = (_: IO[A]) => IO.unit
  )(using EntityDecoder[IO, A]): IO[Unit] = {
    val response = actual.unsafeRunSync()
    for {
      _ <- assertEquals(response.status, expectedStatus).pure[IO]
      _ <- response.body.compile.toVector.map(_.isEmpty).assertEquals(false)
      e <- bodyCheck(response.as[A])
    } yield ()
  }
}
