import Dependencies._

fork := true
name := "stats-collector"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "dev.zio" %% "zio-json" % zioJson,
  "de.heikoseeberger" %% "akka-http-zio-json" % "1.39.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)
