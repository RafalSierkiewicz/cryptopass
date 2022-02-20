def itFilter(name: String): Boolean = name.endsWith("SuiteIt")
def unitFiler(name: String): Boolean = (name.endsWith("Suite"))

lazy val ItTest = config("fun").extend(Test)

lazy val commonSettings =
  Seq(organization := "io.sdev", scalaVersion := "3.1.0", testFrameworks += new TestFramework("munit.Framework"))

lazy val root = project
  .in(file("."))
  .configs(ItTest)
  .settings(name := "playground")
  .settings(commonSettings)
  .aggregate(authority, blog, common, integrationTestCommon)
  .settings(
    inConfig(ItTest)(Defaults.testTasks),
    Test / testOptions := Seq(Tests.Filter(unitFiler)),
    ItTest / testOptions := Seq(Tests.Filter(itFilter))
  )

lazy val authority = (project in file("authority"))
  .settings(name := "authority")
  .configs(ItTest)
  .settings(commonSettings)
  .dependsOn(authorityApi, common)
  .dependsOn(integrationTestCommon % "test->test")
  .settings(libraryDependencies ++= (coreWeb ++ Seq(jwtCirce, web3j)))
  .settings(
    inConfig(ItTest)(Defaults.testTasks),
    Test / testOptions := Seq(Tests.Filter(unitFiler)),
    ItTest / testOptions := Seq(Tests.Filter(itFilter))
  )

lazy val authorityApi = (project in file("authority-api"))
  .settings(name := "authority-api")
  .settings(Compile / PB.targets := Seq(scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"))
  .settings(commonSettings)
  .dependsOn(common)
  .settings(libraryDependencies ++= Seq(http4sClient))

lazy val blog = (project in file("blog"))
  .settings(name := "authority")
  .configs(ItTest)
  .settings(commonSettings)
  .dependsOn(authorityApi, common)
  .dependsOn(integrationTestCommon % "test->test")
  .settings(libraryDependencies ++= (coreWeb ++ Seq(http4sCirce)))
  .settings(
    inConfig(ItTest)(Defaults.testTasks),
    Test / testOptions := Seq(Tests.Filter(unitFiler)),
    ItTest / testOptions := Seq(Tests.Filter(itFilter))
  )

lazy val common = (project in file("common"))
  .settings(name := "common")
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(doobieCore, http4sCirce, scalaPb))

lazy val integrationTestCommon = (project in file("integration-test-common"))
  .settings(name := "integrationTestCommon")
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= (Seq(
      "org.scalameta" %% "munit" % munitVersion,
      "org.typelevel" %% "munit-cats-effect-3" % munitCatsVersion
    ) ++ doobie ++ http4s ++ Seq(testContainers, flyway))
  )

// format: off
val web3jVersion            = "4.8.9"
val doobieVersion           = "1.0.0-RC2"
val http4sVersion           = "0.23.10"
val circeVersion            = "0.14.1"
val pureconfigVersion       = "0.17.1"
val munitVersion            = "0.7.29"
val logbackVersion          = "1.2.10"
val munitCatsVersion        = "1.0.7"
val scalapbVersion          = "0.11.8"

lazy val coreWeb = http4s ++ doobie ++ munit ++ Seq(flyway, pureconfig, circeCore, logback)

lazy val http4s = Seq(http4sClient) ++ Seq(
  "org.http4s"            %%  "http4s-ember-server"     % http4sVersion,
  "org.http4s"            %%  "http4s-dsl"              % http4sVersion
)
lazy val doobie = Seq(
  doobieCore,
  "org.tpolecat"          %%  "doobie-postgres"         % doobieVersion,
  "org.tpolecat"          %%  "doobie-hikari"           % doobieVersion,
  // no scala 3 support
  // "org.tpolecat"          %% "doobie-specs2"            % doobieVersion % Test
)

lazy val munit = Seq(
  "org.scalameta"         %%  "munit"                   % munitVersion             % Test,
  "org.typelevel"         %%  "munit-cats-effect-3"     % munitCatsVersion         % Test
)
lazy val scalaPb        = "com.thesamet.scalapb"  %% "scalapb-runtime"          % scalapbVersion
lazy val doobieCore     = "org.tpolecat"          %%  "doobie-core"             % doobieVersion
lazy val http4sClient   = "org.http4s"            %%  "http4s-ember-client"     % http4sVersion
lazy val http4sCirce    = "org.http4s"            %%  "http4s-circe"            % http4sVersion
lazy val circeCore      = "io.circe"              %%  "circe-generic"           % circeVersion
lazy val jwtCirce       =  "com.github.jwt-scala" %%  "jwt-circe"               % "9.0.3"
lazy val pureconfig     = "com.github.pureconfig" %%  "pureconfig-core"         % pureconfigVersion
lazy val flyway         =  "org.flywaydb"         %   "flyway-core"             % "8.4.4"
lazy val testContainers = "org.testcontainers"    %   "postgresql"              % "1.16.3"        
lazy val web3j          =  "org.web3j"            %   "core"                    % web3jVersion
lazy val logback        =  "ch.qos.logback"       %   "logback-classic"         % logbackVersion           % Runtime



// format: on
