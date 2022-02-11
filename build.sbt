lazy val commonSettings =
  Seq(organization := "io.sdev", scalaVersion := "3.1.0", testFrameworks += new TestFramework("munit.Framework"))

lazy val root = project
  .in(file("."))
  .settings(name := "playground")
  .aggregate(web)
lazy val web = (project in file("cryptopass-web"))
  .settings(commonSettings)
  .settings(
    mainClass in (Compile, run) :=
      Some("io.crypto.pass.app.CryptoPassApp")
  )

lazy val blog = (project in file("blog"))
  .settings(commonSettings)
