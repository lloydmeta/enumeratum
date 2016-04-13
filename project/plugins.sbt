// The Typesafe repository
resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),
  Classpaths.sbtPluginReleases
)
resolvers += Classpaths.sbtPluginReleases

// for code formatting
addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.5")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.1.0")

// Provides the ability to generate unifed documentation for multiple projects
addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.3.1")

// Provides site generation functionality
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.1")

// Provides auto-generating and publishing a gh-pages site
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.3")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.5")