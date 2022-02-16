package io.sdev.common

import org.http4s.EntityDecoder
import cats.effect.kernel.Async
import scalapb.{GeneratedMessageCompanion, GeneratedMessage}

object decoders {

  def protoDecoder[F[_]: Async, A <: GeneratedMessage](using
    model: GeneratedMessageCompanion[A]
  ): EntityDecoder[F, A] = {
    EntityDecoder.byteArrayDecoder.map(bytes => model.parseFrom(bytes))
  }

}
