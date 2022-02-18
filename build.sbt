lazy val commonSettings =
  Seq(organization := "io.sdev", scalaVersion := "3.1.0", testFrameworks += new TestFramework("munit.Framework"))

lazy val root = project
  .in(file("."))
  .settings(name := "playground")
  .settings(commonSettings)
  .aggregate(authority, blog, common)

lazy val authority = (project in file("authority"))
  .settings(name := "authority")
  .settings(commonSettings)
  .dependsOn(authorityApi, common)
  .settings(libraryDependencies ++= (coreWeb ++ Seq(jwtCirce, web3j)))

lazy val authorityApi = (project in file("authority-api"))
  .settings(name := "authority-api")
  .settings(Compile / PB.targets := Seq(scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"))
  .settings(commonSettings)
  .dependsOn(common)
  .settings(libraryDependencies ++= Seq(http4sClient))

lazy val blog = (project in file("blog"))
  .settings(name := "authority")
  .settings(commonSettings)
  .dependsOn(authorityApi, common)
  .settings(libraryDependencies ++= (coreWeb ++ Seq(http4sCirce)))

lazy val common = (project in file("common"))
  .settings(commonSettings)

// format: off
val web3jVersion            = "4.8.9"
val doobieVersion           = "1.0.0-RC2"
val http4sVersion           = "0.23.10"
val circeVersion            = "0.14.1"
val pureconfigVersion       = "0.17.1"
val munitVersion            = "0.7.29"
val logbackVersion          = "1.2.10"
val munitCatsVersion        = "1.0.7"

lazy val coreWeb = http4s ++ doobie ++ munit ++ Seq(flyway, pureconfig, circeCore, logback)

lazy val http4s = Seq(http4sClient) ++ Seq(
  "org.http4s"            %%  "http4s-ember-server"     % http4sVersion,
  "org.http4s"            %%  "http4s-dsl"              % http4sVersion
)
lazy val doobie = Seq(
  "org.tpolecat"          %%  "doobie-core"             % doobieVersion,
  "org.tpolecat"          %%  "doobie-postgres"         % doobieVersion,
  "org.tpolecat"          %%  "doobie-hikari"           % doobieVersion
)

lazy val munit = Seq(
  "org.scalameta"         %%  "munit"                   % munitVersion             % Test,
  "org.typelevel"         %%  "munit-cats-effect-3"     % munitCatsVersion         % Test
)

lazy val http4sClient = "org.http4s"            %%  "http4s-ember-client"     % http4sVersion
lazy val http4sCirce  = "org.http4s"            %%  "http4s-circe"            % http4sVersion
lazy val circeCore    = "io.circe"              %%  "circe-generic"           % circeVersion
lazy val flyway       =  "org.flywaydb"         %   "flyway-core"             % "8.4.4"
lazy val jwtCirce     =  "com.github.jwt-scala" %%  "jwt-circe"               % "9.0.3"
lazy val pureconfig   = "com.github.pureconfig" %%  "pureconfig-core"         % pureconfigVersion
lazy val web3j        =  "org.web3j"            %   "core"                    % web3jVersion
lazy val logback      =  "ch.qos.logback"       %   "logback-classic"         % logbackVersion           % Runtime



// format: on
