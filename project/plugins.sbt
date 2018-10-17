resolvers ++= Seq(
  Classpaths.sbtPluginReleases
)

lazy val neoScalafmtVersion = "1.15"

addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % neoScalafmtVersion)

addSbtPlugin("com.lucidchart" % "sbt-scalafmt-coursier" % neoScalafmtVersion)

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC12")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.22")

addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.2.27")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.3.5")

addSbtPlugin("com.github.tkawachi" % "sbt-doctest" % "0.7.1")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.1")
