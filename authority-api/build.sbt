import Dependencies._

Compile / PB.targets := Seq(scalapb.gen() -> (Compile / sourceManaged).value / "scalapb")

name := "authority-api"

// format: off
libraryDependencies ++= Seq(
    "org.http4s"            %%  "http4s-ember-client"    % http4sVersion
)
// format: on
