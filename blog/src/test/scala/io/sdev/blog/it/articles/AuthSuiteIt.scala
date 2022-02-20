package io.sdev.blog.it.articles

import io.sdev.it.DatabaseMock
import io.sdev.authority.client.AuthorityClient
import cats.effect.IO
import org.http4s.client.Client
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import io.sdev.authority.models.user.DomainUser
import io.sdev.blog.services.AuthService
import io.sdev.it.ResponseVerifier
import io.sdev.authority.models.implicits.{domainUserEntityDecoder, domainUserDecoder}

class AuthSuiteIt extends DatabaseMock with ResponseVerifier {

  test("test auth flow for success") {
    val domainUser = DomainUser(1, "username", "email")
    val clientMock: AuthorityClient[IO] = new AuthorityClient[IO]((null: Client[IO])) {
      def authorize(token: String): IO[DomainUser] = IO.pure(domainUser)
    }
    val authService: AuthService[IO] = new AuthService[IO](clientMock)
    val routes = authService.middleware(authedRoutes).orNotFound

    val request = Request[IO](Method.GET, uri"/").withHeaders(Headers(Header("X-AUTH-TOKEN", "token")))
    val res = routes.run(request)

    check[DomainUser](res, Status.Ok)
  }

  test("test auth flow in case of failure") {
    val domainUser = DomainUser(1, "username", "email")
    val clientMock: AuthorityClient[IO] = new AuthorityClient[IO]((null: Client[IO])) {
      def authorize(token: String): IO[DomainUser] = IO.raiseError(new Exception("random"))
    }
    val authService: AuthService[IO] = new AuthService[IO](clientMock)
    val routes = authService.middleware(authedRoutes).orNotFound

    val request = Request[IO](Method.GET, uri"/").withHeaders(Headers(Header("X-AUTH-TOKEN", "token")))
    val res = routes.run(request)

    check[DomainUser](res, Status.Ok)
  }

  val authedRoutes: AuthedRoutes[DomainUser, IO] =
    AuthedRoutes.of { case GET -> Root as user =>
      Ok(IO.pure(""))
    }

}
