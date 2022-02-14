import Dependencies._

name := "authority"
// format: off
libraryDependencies ++= Seq(
  "org.http4s"            %%  "http4s-ember-server"    % http4sVersion,
  "org.http4s"            %%  "http4s-ember-client"    % http4sVersion,
  "org.http4s"            %%  "http4s-dsl"            % http4sVersion,
  "org.tpolecat"          %%  "doobie-core"           % doobie,
  "org.tpolecat"          %%  "doobie-postgres"       % doobie,
  "org.tpolecat"          %%  "doobie-hikari"         % doobie,
  "org.flywaydb"          %   "flyway-core"           % "8.4.4",
  "com.github.pureconfig" %%  "pureconfig-core"       % pureconfig,
  "org.scalameta"         %%  "munit"                 % munitVersion            % Test,
  "org.typelevel"         %%  "munit-cats-effect-3"   % munitCatsEffectVersion  % Test,
  "ch.qos.logback"        %   "logback-classic"       % logbackVersion          % Runtime
)
// format: on
