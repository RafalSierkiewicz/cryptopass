import Dependencies._

name := "cryptopass-web"
libraryDependencies ++= Seq(
  "io.d11" %% "zhttp" % zioHttp,
  "dev.zio" %% "zio" % zio,
  "dev.zio" %% "zio-json" % "0.3.0-RC1-1",
  "dev.zio" %% "zio-config" % zioConfig,
  "io.getquill" %% "quill-jdbc-zio" % "3.12.0.Beta1.7",
  "dev.zio" %% "zio-config-typesafe" % zioConfig,
  "org.tpolecat" %% "doobie-core" % doobie,
  "org.tpolecat" %% "doobie-h2" % doobie,
  "org.tpolecat" %% "doobie-hikari" % doobie,
  "org.postgresql" % "postgresql" % "42.3.1",
  "org.web3j" % "core" % web3j,
  "org.web3j" % "codegen" % web3j,
  "io.d11" %% "zhttp-test" % zioHttp % Test
)
