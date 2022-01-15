package app

import zhttp.http._
import zhttp.service.Server
import zio._

class CyptoPassApp extends CyptoPassModule {

  val run =
    Server.start(8090, routes).exitCode

  private def routes = {
    Http.collect[Request] {
      case Method.GET -> Root / "text" => Response.text("Hello Worl d ")
    }
  }
}

object CyptoPassApp extends zio.App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    new CyptoPassApp().run
}
