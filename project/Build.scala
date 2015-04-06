import org.scalajs.sbtplugin.cross.CrossType
import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._
import scoverage.ScoverageSbtPlugin.ScoverageKeys._
import com.typesafe.sbt.SbtGhPages.ghpages
import com.typesafe.sbt.SbtSite.site
import sbtunidoc.Plugin.UnidocKeys._
import sbtunidoc.Plugin._
import com.typesafe.sbt.SbtGit.{GitKeys => git}
import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport._

object Enumeratum extends Build {

  lazy val theVersion = "1.1.1-SNAPSHOT"
  lazy val theScalaVersion = "2.11.6"
  lazy val scalaVersions = Seq("2.10.5", "2.11.6")

  lazy val root = Project(id = "enumeratum-root", base = file("."), settings = commonWithPublishSettings)
    .settings(
      name := "enumeratum-root",
      crossScalaVersions := scalaVersions,
      crossVersion := CrossVersion.binary
    )
    .settings(unidocSettings: _*)
    .settings(site.settings ++ ghpages.settings: _*)
    .settings(
      site.addMappingsToSiteDir(
        mappings in (ScalaUnidoc, packageDoc), "latest/api"
      ),
      git.gitRemoteRepo := "git@github.com:lloydmeta/enumeratum.git"
    )
    .settings(
      scalacOptions in (ScalaUnidoc, unidoc) += "-Ymacro-no-expand",
      // Do not publish the root project (it just serves as an aggregate)
      publishArtifact := false,
      publishLocal := {}
    )
    .aggregate(macrosJs, macrosJvm, coreJs, coreJvm, enumeratumPlay, enumeratumPlayJson, enumeratumUPickleJs, enumeratumUPickleJvm)

  lazy val core = crossProject.crossType(CrossType.Pure).in(file("enumeratum-core"))
    .settings(
      name := "enumeratum"
    ).settings(commonWithPublishSettings:_*)
    .settings(utestSettings:_*)
    .dependsOn(macros)
  lazy val coreJs = core.js
  lazy val coreJvm = core.jvm

  lazy val macros = crossProject.crossType(CrossType.Pure).in(file("macros"))
    .settings(commonWithPublishSettings:_*)
    .settings(
      name := "enumeratum-macros",
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value
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
  lazy val macrosJs = macros.js
  lazy val macrosJvm = macros.jvm

  lazy val enumeratumUPickle = crossProject.crossType(CrossType.Pure).in(file("enumeratum-upickle"))
    .settings(commonWithPublishSettings:_*)
    .settings(
      name := "enumeratum-upickle",
      libraryDependencies ++= {
        import org.scalajs.sbtplugin._
        val cross = {
          if (ScalaJSPlugin.autoImport.jsDependencies.?.value.isDefined)
            ScalaJSCrossVersion.binary
          else
            CrossVersion.binary
        }
        Seq(impl.ScalaJSGroupID.withCross("com.lihaoyi", "upickle", cross) % "0.2.8")
        } ++ {
          val additionalMacroDeps = CrossVersion.partialVersion(scalaVersion.value) match {
            // if scala 2.11+ is used, quasiquotes are merged into scala-reflect
            case Some((2, scalaMajor)) if scalaMajor >= 11 =>
              Nil
            // in Scala 2.10, quasiquotes are provided by macro paradise
            case Some((2, 10)) =>
              Seq(
                "org.scalamacros" %% "quasiquotes" % "2.0.1" cross CrossVersion.binary )
          }
          additionalMacroDeps
      }
    )
    .settings(utestSettings:_*)
    .dependsOn(core)
  lazy val enumeratumUPickleJs = enumeratumUPickle.js
  lazy val enumeratumUPickleJvm = enumeratumUPickle.jvm

  lazy val enumeratumPlayJson = Project(id = "enumeratum-play-json", base = file("enumeratum-play-json"), settings = commonWithPublishSettings)
    .settings(commonWithPublishSettings:_*)
    .settings(
      name := "enumeratum-play-json",
      libraryDependencies ++= {
        Seq(
          "com.typesafe.play" %% "play-json" % "2.3.8" % Provided
        )
      }
    )
    .settings(utestSettings: _*)
    .dependsOn(coreJvm)

  lazy val enumeratumPlay = Project(id = "enumeratum-play", base = file("enumeratum-play"), settings = commonWithPublishSettings)
    .settings(
      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play" % "2.3.8" % Provided
      )
    )
    .settings(utestSettings: _*)
    .dependsOn(coreJvm, enumeratumPlayJson)

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

  // Manually expand the macro for %%% in dependencies here so we can share it in all projects
  val utestSettings = {
    Seq(
      libraryDependencies += {
        import org.scalajs.sbtplugin._
        val cross = {
          if (ScalaJSPlugin.autoImport.jsDependencies.?.value.isDefined)
            ScalaJSCrossVersion.binary
          else
            CrossVersion.binary
        }
        impl.ScalaJSGroupID.withCross("com.lihaoyi", "utest", cross) % "0.3.1" % Test
      },
      scalaJSStage in Test := FastOptStage,
      testFrameworks += new TestFramework("utest.runner.Framework")
    )
  }

}
