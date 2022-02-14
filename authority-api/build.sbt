name := "authority-api"
Compile / PB.targets := Seq(scalapb.gen() -> (Compile / sourceManaged).value / "scalapb")

// format: off

// format: on
