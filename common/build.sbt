import Dependencies._

name := "common"
// format: off
libraryDependencies ++= Seq(
  "org.tpolecat"          %%  "doobie-core"           % doobie,
  "org.http4s"            %%  "http4s-circe"          % http4sVersion,
)
// format: on
