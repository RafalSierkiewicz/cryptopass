lazy val commonSettings3 = Seq(
  scalaVersion := "3.1.0"
)

lazy val commonSettings2 = Seq(
  scalaVersion := "2.13.8"
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "playground"
  ).aggregate(
    web,
    collector
  )
lazy val web = (project in file("cryptopass-web"))
  .settings(
    commonSettings3
  ).settings(mainClass in (Compile, run) :=
    Some("io.crypto.pass.app.CryptoPassApp"))

lazy val collector = (project in file("stats-collector"))
  .settings(
    commonSettings2
  ).settings(mainClass in (Compile, run) :=
    Some("io.crypto.CollectorApp"))
