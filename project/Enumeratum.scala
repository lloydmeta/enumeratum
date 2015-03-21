import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._
import scoverage.ScoverageSbtPlugin.ScoverageKeys._

object Enumeratum extends Build {

  lazy val theVersion = "1.1.0-SNAPSHOT"
  lazy val theScalaVersion = "2.11.6"
  lazy val scalaVersions = Seq("2.10.5", "2.11.6")

  lazy val root = Project(id = "enumeratum-root", base = file("."), settings = commonWithPublishSettings)
    .settings(
      name := "enumeratum-root",
      publishArtifact := false,
      crossScalaVersions := scalaVersions,
      crossVersion := CrossVersion.binary
    ).aggregate(macros, core, enumeratumPlay)

  lazy val core = Project(id = "enumeratum", base = file("enumeratum-core"), settings = commonWithPublishSettings)
    .settings(
      name := "enumeratum",
      crossScalaVersions := scalaVersions,
      crossVersion := CrossVersion.binary,
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "2.2.1" % "test"
      )
    ).dependsOn(macros)

  lazy val macros = Project(id = "enumeratum-macros", base = file("macros"), settings = commonWithPublishSettings)
    .settings(
      name := "enumeratum-macros",
      crossScalaVersions := scalaVersions,
      crossVersion := CrossVersion.binary,
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value,
        "org.scalatest" %% "scalatest" % "2.2.1" % "test"
      ) ++ {
        val additionalMacroDeps = CrossVersion.partialVersion(scalaVersion.value) match {
          // if scala 2.11+ is used, quasiquotes are merged into scala-reflect
          case Some((2, scalaMajor)) if scalaMajor >= 11 =>
            Nil
          // in Scala 2.10, quasiquotes are provided by macro paradise
          case Some((2, 10)) =>
            Seq(
              compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full),
              "org.scalamacros" %% "quasiquotes" % "2.0.1" cross CrossVersion.binary)
        }
        additionalMacroDeps }
    )

  lazy val enumeratumPlayJson = Project(id = "enumeratum-play-json", base = file("enumeratum-play-json"), settings = commonWithPublishSettings)
    .settings(
      crossScalaVersions := scalaVersions,
      crossVersion := CrossVersion.binary,
      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play-json" % "2.3.8" % "provided",
        "org.scalatest" %% "scalatest" % "2.2.1" % "test"
      )
    ).dependsOn(core)

  lazy val enumeratumPlay = Project(id = "enumeratum-play", base = file("enumeratum-play"), settings = commonWithPublishSettings)
    .settings(
      crossScalaVersions := scalaVersions,
      crossVersion := CrossVersion.binary,
      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play" % "2.3.8" % "provided",
        "org.scalatest" %% "scalatest" % "2.2.1" % "test"
      )
    ).dependsOn(core,enumeratumPlayJson)


  lazy val commonSettings = Seq(
    organization := "com.beachape",
    version := theVersion,
    scalaVersion := theScalaVersion
  ) ++
    scalariformSettings ++
    scoverageSettings ++
    formatterPrefs ++
    compilerSettings ++
    resolverSettings ++
    ideSettings ++
    testSettings

  lazy val formatterPrefs = Seq(
    ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(AlignParameters, true)
      .setPreference(DoubleIndentClassDeclaration, true)
  )

  lazy val commonWithPublishSettings =
    commonSettings ++
      publishSettings

  lazy val resolverSettings = Seq(
    resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )

  lazy val ideSettings = Seq(
    // Faster "sbt gen-idea"
    transitiveClassifiers in Global := Seq(Artifact.SourceClassifier)
  )

  lazy val compilerSettings = Seq(
    // the name-hashing algorithm for the incremental compiler.
    incOptions := incOptions.value.withNameHashing(nameHashing = true),
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint", "-Xlog-free-terms")
  )

  lazy val testSettings = Seq(Test).flatMap { t =>
    Seq(parallelExecution in t := false) // Avoid DB-related tests stomping on each other
  }

  lazy val scoverageSettings = Seq(
    coverageExcludedPackages := """enumeratum\.EnumMacros""",
    coverageHighlighting := true
  )

  // Settings for publishing to Maven Central
  lazy val publishSettings = Seq(
    pomExtra :=
      <url>https://github.com/lloydmeta/enumeratum</url>
        <licenses>
          <license>
            <name>MIT</name>
            <url>http://opensource.org/licenses/MIT</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:lloydmeta/enumeratum.git</url>
          <connection>scm:git:git@github.com:lloydmeta/enumeratum.git</connection>
        </scm>
        <developers>
          <developer>
            <id>lloydmeta</id>
            <name>Lloyd Chan</name>
            <url>http://lloydmeta.github.io</url>
          </developer>
        </developers>,
    publishTo <<= version { v =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false }
  )


}
