package io.sdev.authority.client

import org.http4s.client.Client
import org.http4s._
import org.http4s.Uri
import cats.effect.kernel.Async
import io.sdev.common.decoders._
import io.sdev.authority.models.user._
import io.sdev.authority.models.implicits.domainUserEntityDecoder

class AuthorityClient[F[_]: Async](client: Client[F]) {

  def authorize(authorizeUser: AuthorizeUser) = {
    val request: Request[F] = Request()
      .withMethod(Method.POST)
      .withUri(Uri.unsafeFromString(s"http://authority:3001/users/authorize"))
      .withEntity(authorizeUser.toByteArray)
    client.expect[DomainUser](request)
  }
}
