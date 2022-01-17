lazy val commonSettings = Seq(
)
ThisBuild / scalaVersion := "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "playground"
  ).aggregate(
    web
  )
lazy val web = (project in file("cryptopass-web"))
  .settings(
    commonSettings
  ).settings(mainClass in (Compile, run) :=
    Some("io.crypto.pass.app.CryptoPassApp"))
