import Dependencies._

name := "common"
// format: off
libraryDependencies ++= Seq(
  "org.tpolecat"          %%  "doobie-core"           % doobie,
  "org.http4s"            %%  "http4s-circe"          % http4sVersion,
  "com.thesamet.scalapb"  %% "scalapb-runtime"        % "0.11.8" 
)
// format: on
