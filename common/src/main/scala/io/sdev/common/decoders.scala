package io.sdev.common

import org.http4s.EntityDecoder
import cats.effect.kernel.Async
import scalapb.{GeneratedMessageCompanion, GeneratedMessage}

object decoders {
  trait ProtobufDecoder[A] {
    def decode(bytes: Array[Byte]): A
  }

  def protoDecoder[A <: GeneratedMessage](using model: GeneratedMessageCompanion[A]) = new ProtobufDecoder[A] {
    def decode(bytes: Array[Byte]): A = model.parseFrom(bytes)
  }

  def entityDecoder[F[_]: Async, A](using decoder: ProtobufDecoder[A]): EntityDecoder[F, A] =
    EntityDecoder.byteArrayDecoder.map(bytes => decoder.decode(bytes))
}
