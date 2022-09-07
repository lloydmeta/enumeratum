resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scalameta"       % "sbt-scalafmt"  % "2.4.6")
addSbtPlugin("com.typesafe.sbt"    % "sbt-git"       % "1.0.0")
addSbtPlugin("pl.project13.scala"  % "sbt-jmh"       % "0.4.3")
addSbtPlugin("com.github.tkawachi" % "sbt-doctest"   % "0.9.9")
addSbtPlugin("com.jsuereth"        % "sbt-pgp"       % "1.1.2")
addSbtPlugin("com.eed3si9n"        % "sbt-buildinfo" % "0.9.0")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.10.1")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.2.0")

addSbtPlugin(("org.scoverage" % "sbt-scoverage" % "2.0.2").exclude("org.scala-lang.modules", "*"))

addSbtPlugin(("org.scoverage" % "sbt-coveralls" % "1.3.2").exclude("org.scala-lang.modules", "*"))
