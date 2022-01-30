import Dependencies._

fork := true
name := "stats-collector"

libraryDependencies ++= Seq(
  "com.typesafe.akka"          %% "akka-http"           % akkaHttpVersion,
  "com.typesafe.akka"          %% "akka-actor-typed"    % akkaVersion,
  "com.typesafe.akka"          %% "akka-stream"         % akkaVersion,
  "dev.zio"                    %% "zio-json"            % zioJson,
  "com.typesafe.scala-logging" %% "scala-logging"       % "3.9.4",
  "dev.zio"                    %% "zio-config"          % zioConfig,
  "dev.zio"                    %% "zio-config-typesafe" % zioConfig,
  "de.heikoseeberger"          %% "akka-http-zio-json"  % "1.39.2",
  "ch.qos.logback"              % "logback-classic"     % "1.2.3",
  "org.web3j"                   % "core"                % web3j,
  "joda-time"                   % "joda-time"           % "2.10.13"
)
