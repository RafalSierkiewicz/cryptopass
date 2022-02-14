package io.sdev.authority.client

import org.http4s.client.Client
import io.sdev.authority.models.user.User
import org.http4s._
import org.http4s.Uri._
import org.http4s.Uri
import cats.effect.kernel.Async

class AuthorityClient[F[_]: Async](client: Client[F]) {
  private val decoder: EntityDecoder[F, User] =
    EntityDecoder.byteArrayDecoder[F].map { b =>
      User.parseFrom(b)
    }

  def getUser(email: String) = {
    client.expect[User](Request(Method.GET, Uri.unsafeFromString(s"http://authority:3001/users/$email")))(decoder)
  }
}
