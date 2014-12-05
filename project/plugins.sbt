// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Classpaths.sbtPluginReleases

// for code formatting
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")