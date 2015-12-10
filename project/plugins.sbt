// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

resolvers += Classpaths.sbtPluginReleases

// for code formatting
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.1")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.0.0.BETA1")

// Provides the ability to generate unifed documentation for multiple projects
addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.3.1")

// Provides site generation functionality
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.1")

// Provides auto-generating and publishing a gh-pages site
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.3")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.5")