import Dependencies._

name := "cryptopass-web"
libraryDependencies ++= Seq(
  "io.d11" %% "zhttp" % zioHttp,
  "dev.zio" %% "zio" % zio,
  "dev.zio" %% "zio-json" % "0.2.0-M3",
  "org.web3j" % "core" % web3j,
  "org.web3j" % "codegen" % web3j,
  "io.d11" %% "zhttp-test" % zioHttp % Test
)
