package io.crypto.common

import scala.concurrent.duration.FiniteDuration

import java.util.concurrent.TimeUnit
import org.joda.time.DateTime
import zio.json._

object Codecs {
  private val dateTimeDecoder: JsonDecoder[DateTime]             =
    JsonDecoder[Long].map(millis => new DateTime(millis))

  private val dateTimeEncoder: JsonEncoder[DateTime]             =
    JsonEncoder[Long].contramap(_.getMillis())

  private val finiteDurationDecoder: JsonDecoder[FiniteDuration] =
    JsonDecoder[Long].map(millis => FiniteDuration(millis, TimeUnit.SECONDS))

  private val finiteDurationEncoder: JsonEncoder[FiniteDuration] =
    JsonEncoder[Long].contramap(_.toMillis)

  implicit val dateTimeCodec                                     = JsonCodec(dateTimeEncoder, dateTimeDecoder)
  implicit val finiteDurationCodec = JsonCodec(finiteDurationEncoder, finiteDurationDecoder)
}
