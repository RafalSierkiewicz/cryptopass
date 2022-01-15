import Dependencies._

name := "cryptopass-web"
libraryDependencies ++= Seq(
  "io.d11" %% "zhttp" % zioHttp,
  "dev.zio" %% "zio" % zio,
  "io.d11" %% "zhttp-test" % zioHttp % Test
)
