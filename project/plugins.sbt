resolvers ++= Seq(
  Classpaths.sbtPluginReleases
)

addSbtPlugin("com.geirsson"        % "sbt-scalafmt"             % "1.5.1")
addSbtPlugin("org.scoverage"       % "sbt-scoverage"            % "1.5.1")
addSbtPlugin("org.scoverage"       % "sbt-coveralls"            % "1.2.7")
addSbtPlugin("com.typesafe.sbt"    % "sbt-git"                  % "1.0.0")
addSbtPlugin("org.scala-js"        % "sbt-scalajs"              % "0.6.28")
addSbtPlugin("org.portable-scala"  % "sbt-scalajs-crossproject" % "0.6.0")
addSbtPlugin("pl.project13.scala"  % "sbt-jmh"                  % "0.3.6")
addSbtPlugin("com.github.tkawachi" % "sbt-doctest"              % "0.7.2")
addSbtPlugin("com.jsuereth"        % "sbt-pgp"                  % "1.1.2")
addSbtPlugin("com.eed3si9n"        % "sbt-buildinfo"            % "0.9.0")
