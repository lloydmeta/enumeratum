resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scalameta"       % "sbt-scalafmt"  % "2.5.0")
addSbtPlugin("com.github.sbt"      % "sbt-git"       % "2.0.1")
addSbtPlugin("pl.project13.scala"  % "sbt-jmh"       % "0.4.5")
addSbtPlugin("com.github.tkawachi" % "sbt-doctest"   % "0.10.0")
addSbtPlugin("com.github.sbt"      % "sbt-pgp"       % "2.2.1")
addSbtPlugin("com.eed3si9n"        % "sbt-buildinfo" % "0.11.0")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.16.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")

addSbtPlugin("org.scala-native"   % "sbt-scala-native"              % "0.5.4")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.3.2")

addSbtPlugin(("org.scoverage" % "sbt-scoverage" % "2.0.8").exclude("org.scala-lang.modules", "*"))
