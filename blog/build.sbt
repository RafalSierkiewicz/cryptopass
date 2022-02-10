import Dependencies._

name := "blog"
// format: off
libraryDependencies ++= Seq(
  "org.http4s"      %%  "http4s-ember-server"   % http4sVersion,
  "org.http4s"      %%  "http4s-ember-client"   % http4sVersion,
  "org.http4s"      %%  "http4s-circe"          % http4sVersion,
  "org.http4s"      %%  "http4s-dsl"            % http4sVersion,
  "io.circe"        %%  "circe-generic"         % circeVersion,
  "org.scalameta"   %%  "munit"                 % munitVersion            % Test,
  "org.typelevel"   %%  "munit-cats-effect-3"   % munitCatsEffectVersion  % Test,
  "ch.qos.logback"  %   "logback-classic"       % logbackVersion          % Runtime
)
// format: on
