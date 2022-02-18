package io.sdev.authority

import cats.effect.{ExitCode, IO, IOApp}
import cats.effect.kernel.Async

object Authority extends IOApp {
  def run(args: List[String]) =
    AuthorityServer.stream[IO].compile.drain.as(ExitCode.Success)
}
