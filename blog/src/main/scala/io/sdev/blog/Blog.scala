package io.sdev.blog

import cats.effect.{ExitCode, IO, IOApp}

object Blog extends IOApp {
  def run(args: List[String]) =
    BlogServer.stream[IO].compile.drain.as(ExitCode.Success)
}
