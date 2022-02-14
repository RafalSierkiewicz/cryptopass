lazy val commonSettings =
  Seq(organization := "io.sdev", scalaVersion := "3.1.0", testFrameworks += new TestFramework("munit.Framework"))

lazy val root = project
  .in(file("."))
  .settings(name := "playground")
  .settings(commonSettings)
  .aggregate(authority, blog, common, web)

lazy val authority = (project in file("authority"))
  .settings(commonSettings)
  .dependsOn(authorityApi, common)

lazy val authorityApi = (project in file("authority-api"))
  .settings(commonSettings)

lazy val blog = (project in file("blog"))
  .settings(commonSettings)
  .dependsOn(authorityApi, common)

lazy val common = (project in file("common"))
  .settings(commonSettings)

lazy val web = (project in file("cryptopass-web"))
  .settings(commonSettings)
