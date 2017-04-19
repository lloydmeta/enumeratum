resolvers ++= Seq(
  Classpaths.sbtPluginReleases
)

addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "0.6.5")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.15")

addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.2.24")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.0.2")

addSbtPlugin("com.github.tkawachi" % "sbt-doctest" % "0.5.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-M15")