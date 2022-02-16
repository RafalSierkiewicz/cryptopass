package io.sdev.authority.client

import org.http4s.client.Client
import org.http4s._
import org.http4s.Uri
import cats.effect.kernel.Async
import io.sdev.common.decoders._
import io.sdev.authority.models.user._

class AuthorityClient[F[_]: Async](client: Client[F]) {
  given EntityDecoder[F, UserId] = protoDecoder[F, UserId]

  def createUser(newUser: UserCreate) = {
    val request: Request[F] = Request()
      .withMethod(Method.POST)
      .withUri(Uri.unsafeFromString(s"http://authority:3001/users"))
      .withEntity(newUser.toByteArray)
    client.expect[UserId](request)
  }
}
