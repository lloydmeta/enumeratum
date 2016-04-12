// The Typesafe repository
resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),
  Classpaths.sbtPluginReleases
)
resolvers += Classpaths.sbtPluginReleases

// for code formatting
addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")

// SBT-Scoverage version must be compatible with SBT-coveralls version below
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.1")

// Upgrade when this issue is solved https://github.com/scoverage/sbt-coveralls/issues/73
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.0.0")

// Provides the ability to generate unifed documentation for multiple projects
addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.3.1")

// Provides site generation functionality
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.1")

// Provides auto-generating and publishing a gh-pages site
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.3")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.5")