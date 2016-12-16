resolvers ++= Seq(
  Classpaths.sbtPluginReleases
)

addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "0.4.9")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.13")

addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.2.6")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "1.2.1")
