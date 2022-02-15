package io.sdev.authority.client

import org.http4s.client.Client
import io.sdev.authority.models.user.User
import org.http4s._
import org.http4s.Uri
import cats.effect.kernel.Async
import io.sdev.common.decoders._

class AuthorityClient[F[_]: Async](client: Client[F]) {
  given ProtobufDecoder[User] = protoDecoder[User]

  def getUser(email: String) = {
    client.expect[User](Request(Method.GET, Uri.unsafeFromString(s"http://authority:3001/users/$email")))(
      entityDecoder[F, User]
    )
  }
}
