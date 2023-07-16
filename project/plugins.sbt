resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scalameta"       % "sbt-scalafmt"  % "2.4.6")
addSbtPlugin("com.typesafe.sbt"    % "sbt-git"       % "1.0.0")
addSbtPlugin("pl.project13.scala"  % "sbt-jmh"       % "0.4.3")
addSbtPlugin("com.github.tkawachi" % "sbt-doctest"   % "0.10.0")
addSbtPlugin("com.github.sbt"      % "sbt-pgp"       % "2.2.1")
addSbtPlugin("com.eed3si9n"        % "sbt-buildinfo" % "0.9.0")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.10.1")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.2.0")

addSbtPlugin("org.scala-native"   % "sbt-scala-native"              % "0.4.14")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.2.0")

addSbtPlugin(("org.scoverage" % "sbt-scoverage" % "2.0.8").exclude("org.scala-lang.modules", "*"))

addSbtPlugin(("org.scoverage" % "sbt-coveralls" % "1.3.2").exclude("org.scala-lang.modules", "*"))
