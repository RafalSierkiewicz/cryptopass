import Dependencies._

name := "cryptopass-web"
libraryDependencies ++= Seq(
  "io.d11" %% "zhttp" % zioHttp,
  "dev.zio" %% "zio" % zio,
  "dev.zio" %% "zio-json" % "0.3.0-RC1-1",
  "dev.zio" %% "zio-config" % zioConfig,
  "dev.zio" %% "zio-config-typesafe" % zioConfig,
  "org.postgresql" % "postgresql" % "42.3.1",
  "org.web3j" % "core" % web3j,
  "org.web3j" % "codegen" % web3j,
  "io.d11" %% "zhttp-test" % zioHttp % Test
)
