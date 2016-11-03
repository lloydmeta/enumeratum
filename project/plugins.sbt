resolvers ++= Seq(
  Classpaths.sbtPluginReleases
)

// TODO add back Scalafmt for code formatting
//addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "0.4.1")

// TODO figure out when this is going to support 2.12
// addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.4.0")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.13")

addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.2.6")

addSbtPlugin("com.updateimpact" % "updateimpact-sbt-plugin" % "2.1.1")
