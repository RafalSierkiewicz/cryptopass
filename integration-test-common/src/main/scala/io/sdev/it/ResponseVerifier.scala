package io.sdev.it
import munit.CatsEffectSuite
import cats.effect.IO
import org.http4s._
import cats.implicits._

trait ResponseVerifier { self: CatsEffectSuite =>

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
