import com.typesafe.sbt.SbtGit.{GitKeys => git}
import sbtbuildinfo.BuildInfoPlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

lazy val scala_2_11Version = "2.11.12"
lazy val scala_2_12Version = "2.12.11"
lazy val scala_2_13Version = "2.13.2"
lazy val scalaVersionsAll  = Seq(scala_2_11Version, scala_2_12Version, scala_2_13Version)

lazy val theScalaVersion = scala_2_12Version

lazy val scalaTestVersion  = "3.1.1"
lazy val scalacheckVersion = "1.14.3"

// Library versions
lazy val reactiveMongoVersion = "0.20.3"
lazy val argonautVersion      = "6.2.3"
lazy val json4sVersion        = "3.6.6"
lazy val quillVersion         = "3.5.0"

def theDoobieVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor >= 12 => "0.8.8"
    case Some((2, scalaMajor)) if scalaMajor == 11 => "0.7.0"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion")
  }

def theArgonautVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 => "6.2.3"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion")
  }

def thePlayVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor >= 13 => "2.8.0"
    case Some((2, scalaMajor)) if scalaMajor >= 11 => "2.7.0"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion")
  }

def theSlickVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 => "3.3.2"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion")
  }

def theCatsVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor >= 12 => "2.1.1"
    case Some((2, scalaMajor)) if scalaMajor >= 11 => "2.0.0"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion")
  }

def thePlayJsonVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor >= 13 => "2.8.1"
    case Some((2, scalaMajor)) if scalaMajor >= 11 => "2.7.3"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion")
  }

def theCirceVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor >= 12 => "0.13.0"
    case Some((2, scalaMajor)) if scalaMajor >= 11 => "0.11.1"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion")
  }

def scalaTestPlay(scalaVersion: String) = CrossVersion.partialVersion(scalaVersion) match {
  case Some((2, scalaMajor)) if scalaMajor >= 13 =>
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
  case Some((2, scalaMajor)) if scalaMajor >= 11 =>
    "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
  case _ =>
    throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion")
}

lazy val baseProjectRefs =
  Seq(macrosJS, macrosJVM, coreJS, coreJVM, coreJVMTests).map(Project.projectToRef)

lazy val scala213ProjectRefs = Seq(
  enumeratumJson4s,
  enumeratumScalacheckJvm,
  enumeratumScalacheckJs,
  enumeratumPlayJsonJvm,
  // enumeratumPlayJsonJs, TODO re-enable once play-json supports Scala.js 1.0
  // enumeratumArgonautJs, TODO re-enable once argonaut supports Scala.js 1.0
  enumeratumArgonautJvm,
  enumeratumSlick,
  enumeratumPlay,
  enumeratumCirceJvm,
  enumeratumCirceJs,
  enumeratumReactiveMongoBson,
  enumeratumCatsJvm,
  enumeratumCatsJs,
  enumeratumQuillJvm,
  // enumeratumQuillJs  TODO re-enable once quill supports Scala.js 1.0
).map(Project.projectToRef)

lazy val scala_2_13 = Project(id = "scala_2_13", base = file("scala_2_13"))
  .settings(
    commonSettings ++ publishSettings,
    name := "enumeratum-scala_2_13",
    scalaVersion := scala_2_13Version, // not sure if this and below are needed
    crossScalaVersions := Seq(scala_2_13Version),
    crossVersion := CrossVersion.binary,
    // Do not publish this  project (it just serves as an aggregate)
    publishArtifact := false,
    publishLocal := {},
    //doctestWithDependencies := false, // sbt-doctest is not yet compatible with this 2.13
    aggregate in publish := false,
    aggregate in PgpKeys.publishSigned := false
  )
  .aggregate((baseProjectRefs ++ scala213ProjectRefs): _*)

lazy val integrationProjectRefs = Seq(
  enumeratumPlay,
//  enumeratumPlayJsonJs, TODO re-enable once play-json supports Scala.js 1.0
  enumeratumPlayJsonJvm,
  enumeratumCirceJs,
  enumeratumCirceJvm,
  enumeratumReactiveMongoBson,
//  enumeratumArgonautJs,  TODO re-enable once argonaut supports Scala.js 1.0
  enumeratumArgonautJvm,
  enumeratumJson4s,
  enumeratumScalacheckJs,
  enumeratumScalacheckJvm,
//  enumeratumQuillJs, TODO re-enable once quill supports Scala.js 1.0
  enumeratumQuillJvm,
  enumeratumDoobie,
  enumeratumSlick,
  enumeratumCatsJs,
  enumeratumCatsJvm
).map(Project.projectToRef)

lazy val root =
  Project(id = "enumeratum-root", base = file("."))
    .settings(commonWithPublishSettings: _*)
    .settings(
      name := "enumeratum-root",
      crossVersion := CrossVersion.binary,
      crossScalaVersions := Nil,
      git.gitRemoteRepo := "git@github.com:lloydmeta/enumeratum.git",
      // Do not publish the root project (it just serves as an aggregate)
      publishArtifact := false,
      publishLocal := {},
      aggregate in publish := false,
      aggregate in PgpKeys.publishSigned := false
    )
    .aggregate(baseProjectRefs ++ integrationProjectRefs: _*)

lazy val macrosAggregate = aggregateProject("macros", macrosJS, macrosJVM)
lazy val macros = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("macros"))
  .settings(testSettings: _*)
  .jsSettings(jsTestSettings: _*)
  .settings(commonWithPublishSettings: _*)
  .settings(withCompatUnmanagedSources(jsJvmCrossProject = true, includeTestSrcs = false): _*)
  .settings(
    name := "enumeratum-macros",
    version := Versions.Macros.head,
    crossScalaVersions := scalaVersionsAll, // eventually move this to aggregateProject once more 2.13 libs are out
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    )
  )

lazy val macrosJS  = macros.js
lazy val macrosJVM = macros.jvm

// Aggregates core
lazy val coreAggregate = aggregateProject("core", coreJS, coreJVM)
lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-core"))
  .settings(testSettings: _*)
  .jsSettings(jsTestSettings: _*)
  .settings(commonWithPublishSettings: _*)
  .settings(
    name := "enumeratum",
    version := Versions.Core.head,
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies += "com.beachape" %% "enumeratum-macros" % Versions.Macros.stable
  )
  .dependsOn(macros) // used for testing macros
lazy val coreJS  = core.js
lazy val coreJVM = core.jvm

lazy val testsAggregate = aggregateProject("test", enumeratumTestJs, enumeratumTestJvm)
// Project models used in test for some subprojects
lazy val enumeratumTest = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-test"))
  .settings(testSettings: _*)
  .jsSettings(jsTestSettings: _*)
  .settings(commonWithPublishSettings: _*)
  .settings(
    name := "enumeratum-test",
    version := Versions.Core.head,
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies += {
      "com.beachape" %%% "enumeratum" % Versions.Core.stable
    }
  )
  .dependsOn(core)
lazy val enumeratumTestJs  = enumeratumTest.js
lazy val enumeratumTestJvm = enumeratumTest.jvm

lazy val coreJVMTests = Project(id = "coreJVMTests", base = file("enumeratum-core-jvm-tests"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name,
                                       version,
                                       scalaVersion,
                                       sbtVersion,
                                       BuildInfoKey.action("macrosJVMClassesDir") {
                                         ((macrosJVM / classDirectory) in Compile).value
                                       }),
    buildInfoPackage := "enumeratum"
  )
  .settings(commonWithPublishSettings: _*)
  .settings(testSettings: _*)
  .settings(
    name := "coreJVMTests",
    version := Versions.Core.head,
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-compiler" % scalaVersion.value % Test
    ),
    publishArtifact := false,
    publishLocal := {}
  )
  .dependsOn(coreJVM, macrosJVM)

lazy val enumeratumReactiveMongoBson =
  Project(id = "enumeratum-reactivemongo-bson", base = file("enumeratum-reactivemongo-bson"))
    .settings(commonWithPublishSettings: _*)
    .settings(testSettings: _*)
    .settings(
      version := "1.6.1-SNAPSHOT",
      crossScalaVersions := scalaVersionsAll,
      libraryDependencies ++= Seq(
        "org.reactivemongo" %% "reactivemongo-bson-api" % s"$reactiveMongoVersion-noshaded" % Provided,
        "com.beachape"      %% "enumeratum"             % Versions.Core.stable,
        "com.beachape"      %% "enumeratum-test"        % Versions.Core.stable % Test
      )
    )

lazy val playJsonAggregate =
  aggregateProject("play-json", /*enumeratumPlayJsonJs,*/ enumeratumPlayJsonJvm)
    .settings( // TODO re-enable once play-json supports Scala.js 1.0
      crossScalaVersions := scalaVersionsAll)
lazy val enumeratumPlayJson = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-play-json"))
  .settings(commonWithPublishSettings: _*)
  .settings(testSettings: _*)
  .jsSettings(jsTestSettings: _*)
  .settings(
    name := "enumeratum-play-json",
    version := "1.6.1-SNAPSHOT",
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies ++= {
      Seq(
        "com.typesafe.play" %%% "play-json"       % thePlayJsonVersion(scalaVersion.value),
        "com.beachape"      %%% "enumeratum"      % Versions.Core.stable,
        "com.beachape"      %%% "enumeratum-test" % Versions.Core.stable % Test
      )
    }
  )
// lazy val enumeratumPlayJsonJs  = enumeratumPlayJson.js // TODO re-enable once play-json supports Scala.js 1.0
lazy val enumeratumPlayJsonJvm = enumeratumPlayJson.jvm

lazy val enumeratumPlay = Project(id = "enumeratum-play", base = file("enumeratum-play"))
  .settings(commonWithPublishSettings: _*)
  .settings(testSettings: _*)
  .settings(
    version := "1.6.1-SNAPSHOT",
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies ++=
      Seq(
        "com.typesafe.play" %% "play"            % thePlayVersion(scalaVersion.value),
        "com.beachape"      %% "enumeratum"      % Versions.Core.stable,
        "com.beachape"      %% "enumeratum-test" % Versions.Core.stable % Test,
        scalaTestPlay(scalaVersion.value)
      )
  )
  .settings(withCompatUnmanagedSources(jsJvmCrossProject = false, includeTestSrcs = true): _*)
  .dependsOn(enumeratumPlayJsonJvm % "compile->compile;test->test")

lazy val circeAggregate = aggregateProject("circe", enumeratumCirceJs, enumeratumCirceJvm)
lazy val enumeratumCirce = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-circe"))
  .settings(commonWithPublishSettings: _*)
  .settings(testSettings: _*)
  .jsSettings(jsTestSettings: _*)
  .settings(
    name := "enumeratum-circe",
    version := "1.6.1-SNAPSHOT",
    libraryDependencies ++= {
      Seq(
        "com.beachape" %%% "enumeratum" % Versions.Core.stable,
        "io.circe"     %%% "circe-core" % theCirceVersion(scalaVersion.value)
      )
    }
  )
  .jvmSettings(
    crossScalaVersions := scalaVersionsAll
  )
  .jsSettings(
    crossScalaVersions := Seq(scala_2_12Version, scala_2_13Version)
  )
  .dependsOn(core)

lazy val enumeratumCirceJs  = enumeratumCirce.js
lazy val enumeratumCirceJvm = enumeratumCirce.jvm

lazy val argonautAggregate =
  aggregateProject("argonaut", /*enumeratumArgonautJs,*/ enumeratumArgonautJvm) // TODO re-enable once argonaut supports Scala.js 1.0
lazy val enumeratumArgonaut = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-argonaut"))
  .settings(commonWithPublishSettings: _*)
  .settings(testSettings: _*)
  .jsSettings(jsTestSettings: _*)
  .settings(
    name := "enumeratum-argonaut",
    version := "1.6.1-SNAPSHOT",
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies ++= {
      Seq(
        "com.beachape" %%% "enumeratum" % Versions.Core.stable,
        "io.argonaut"  %%% "argonaut"   % theArgonautVersion(scalaVersion.value)
      )
    }
  )

// lazy val enumeratumArgonautJs  = enumeratumArgonaut.js // TODO re-enable once argonaut supports Scala.js 1.0
lazy val enumeratumArgonautJvm = enumeratumArgonaut.jvm

lazy val enumeratumJson4s =
  Project(id = "enumeratum-json4s", base = file("enumeratum-json4s"))
    .settings(commonWithPublishSettings: _*)
    .settings(testSettings: _*)
    .settings(
      version := "1.6.1-SNAPSHOT",
      crossScalaVersions := scalaVersionsAll,
      libraryDependencies ++= Seq(
        "org.json4s"   %% "json4s-core"   % json4sVersion,
        "org.json4s"   %% "json4s-native" % json4sVersion % Test,
        "com.beachape" %% "enumeratum"    % Versions.Core.stable
      )
    )

lazy val scalacheckAggregate =
  aggregateProject("scalacheck", enumeratumScalacheckJs, enumeratumScalacheckJvm)

lazy val enumeratumScalacheck = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-scalacheck"))
  .settings(commonWithPublishSettings: _*)
  .settings(testSettings: _*)
  .jsSettings(jsTestSettings: _*)
  .settings(
    name := "enumeratum-scalacheck",
    version := "1.6.1-SNAPSHOT",
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies ++= {
      Seq(
        "com.beachape"   %%% "enumeratum"      % Versions.Core.stable,
        "org.scalacheck" %%% "scalacheck"      % scalacheckVersion,
        "org.scalatestplus" %%% "scalacheck-1-14" % "3.1.1.1" % Test,
        "com.beachape"   %%% "enumeratum-test" % Versions.Core.stable % Test
      )
    }
  )
  .dependsOn(enumeratumTest)

lazy val enumeratumScalacheckJs  = enumeratumScalacheck.js
lazy val enumeratumScalacheckJvm = enumeratumScalacheck.jvm

lazy val quillAggregate =
  aggregateProject("quill", /*enumeratumQuillJs,*/ enumeratumQuillJvm) // TODO re-enable once quill supports Scala.js 1.0
    .settings(crossScalaVersions := scalaVersionsAll)
lazy val enumeratumQuill = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-quill"))
  .settings(commonWithPublishSettings: _*)
  .settings(testSettings: _*)
  .jsSettings(jsTestSettings: _*)
  .settings(
    name := "enumeratum-quill",
    version := "1.6.1-SNAPSHOT",
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies ++= {
      Seq(
        "com.beachape" %%% "enumeratum" % Versions.Core.stable,
        "io.getquill"  %%% "quill-core" % quillVersion,
        "io.getquill"  %%% "quill-sql"  % quillVersion % Test
      )
    },
    dependencyOverrides ++= {
      def pprintVersion(v: String) = {
        if (v.startsWith("2.11")) "0.5.4" else "0.5.5"
      }
      Seq(
        "com.lihaoyi" %%% "pprint" % pprintVersion(scalaVersion.value)
      )
    }
  )
// lazy val enumeratumQuillJs  = enumeratumQuill.js // TODO re-enable once quill supports Scala.js 1.0
lazy val enumeratumQuillJvm = enumeratumQuill.jvm

lazy val enumeratumDoobie =
  Project(id = "enumeratum-doobie", base = file("enumeratum-doobie"))
    .settings(commonWithPublishSettings: _*)
    .settings(testSettings: _*)
    .settings(
      crossScalaVersions := scalaVersionsAll,
      version := "1.6.1-SNAPSHOT",
      libraryDependencies ++= {
        Seq(
          "com.beachape" %%% "enumeratum" % Versions.Core.stable,
          "org.tpolecat" %% "doobie-core" % theDoobieVersion(scalaVersion.value)
        )
      }
    )

lazy val enumeratumSlick =
  Project(id = "enumeratum-slick", base = file("enumeratum-slick"))
    .settings(commonWithPublishSettings: _*)
    .settings(testSettings: _*)
    .settings(
      version := "1.6.1-SNAPSHOT",
      crossScalaVersions := scalaVersionsAll,
      libraryDependencies ++= Seq(
        "com.typesafe.slick" %% "slick"      % theSlickVersion(scalaVersion.value),
        "com.beachape"       %% "enumeratum" % Versions.Core.stable,
        "com.h2database"     % "h2"          % "1.4.197" % Test
      )
    )

lazy val catsAggregate = aggregateProject("cats", enumeratumCatsJs, enumeratumCatsJvm)
lazy val enumeratumCats = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-cats"))
  .settings(commonWithPublishSettings: _*)
  .settings(testSettings: _*)
  .jsSettings(jsTestSettings: _*)
  .settings(
    name := "enumeratum-cats",
    version := "1.6.1-SNAPSHOT",
    libraryDependencies ++= {
      Seq(
        "com.beachape"  %%% "enumeratum" % Versions.Core.stable,
        "org.typelevel" %%% "cats-core"  % theCatsVersion(scalaVersion.value)
      )
    }
  )
  .jvmSettings(
    crossScalaVersions := scalaVersionsAll
  )
  .jsSettings(
    crossScalaVersions := Seq(scala_2_12Version, scala_2_13Version)
  )
  .dependsOn(core)

lazy val enumeratumCatsJs  = enumeratumCats.js
lazy val enumeratumCatsJvm = enumeratumCats.jvm

lazy val commonSettings = Seq(
  organization := "com.beachape",
  scalafmtOnCompile := true,
  scalaVersion := theScalaVersion,
  crossScalaVersions := scalaVersionsAll
) ++
  compilerSettings ++
  resolverSettings ++
  ideSettings

lazy val commonSettingsWithTrimmings =
  commonSettings ++
    scoverageSettings

lazy val commonWithPublishSettings =
  commonSettingsWithTrimmings ++
    publishSettings

lazy val resolverSettings = Seq(
  resolvers ++= Seq(
    "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/",
    "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases"
  )
)

lazy val ideSettings = Seq(
  // Faster "sbt gen-idea"
  transitiveClassifiers in Global := Seq(Artifact.SourceClassifier)
)

lazy val compilerSettings = Seq(
  scalaJSStage in ThisBuild := {
    sys.props.get("sbt.scalajs.testOpt").orElse(sys.env.get("SCALAJS_TEST_OPT")) match {
      case Some("full") => FullOptStage
      case _            => FastOptStage
    }
  },
  scalacOptions in (Compile, compile) ++= {
    val base = Seq(
      "-Xlog-free-terms",
      "-encoding",
      "UTF-8", // yes, this is 2 args
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-unchecked",
      "-Xfatal-warnings",
//      "-Ywarn-adapted-args",
      "-Ywarn-dead-code", // N.B. doesn't work well with the ??? hole
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Xfuture"
    )
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, m)) if m >= 13 =>
        base.filterNot(flag => flag == "-Xfatal-warnings" || flag == "-Xfuture") ++ // todo see how to disable deprecations in 2.13.x
          Seq( /*"-deprecation:false", */ "-Xlint:-unused,_") // unused-import breaks Circe Either shim
      case Some((2, m)) if m >= 12 =>
        base ++ Seq("-deprecation:false", "-Xlint:-unused,_") // unused-import breaks Circe Either shim
      case Some((2, 11)) => base ++ Seq("-deprecation:false", "-Xlint", "-Ywarn-unused-import")
      case _             => base ++ Seq("-Xlint")
    }
  }
)

lazy val scoverageSettings = Seq(
  coverageExcludedPackages := """enumeratum\.EnumMacros;enumeratum\.ContextUtils;enumeratum\.ValueEnumMacros""",
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
      <developers>
        <developer>
          <id>lloydmeta</id>
          <name>Lloyd Chan</name>
          <url>https://beachape.com</url>
        </developer>
      </developers>,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (version.value.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toCharArray),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  PgpKeys.pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toCharArray),
  pomIncludeRepository := { _ =>
    false
  }
)

val testSettings = {
  Seq(
    libraryDependencies ++= {
      Seq(
        "org.scalatest" %%% "scalatest" % scalaTestVersion % Test
      )
    },
    doctestGenTests := {
      val originalValue = doctestGenTests.value
      Seq.empty // TODO: re-enable originalValue
    },
    doctestTestFramework := DoctestTestFramework.ScalaTest
  )
}

val jsTestSettings = {
  Seq(
    doctestGenTests := {
      Seq.empty
    },
  )
}

lazy val benchmarking =
  Project(id = "benchmarking", base = file("benchmarking"))
    .settings(commonWithPublishSettings: _*)
    .settings(
      name := "benchmarking",
      crossVersion := CrossVersion.binary,
      // Do not publish
      publishArtifact := false,
      publishLocal := {}
    )
    .dependsOn((baseProjectRefs ++ integrationProjectRefs).map(ClasspathDependency(_, None)): _*)
    .enablePlugins(JmhPlugin)
    .settings(libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.21")

/**
  * Helper function to add unmanaged source compat directories for different scala versions
  */
def withCompatUnmanagedSources(jsJvmCrossProject: Boolean,
                               includeTestSrcs: Boolean): Seq[Setting[_]] = {
  def compatDirs(projectbase: File, scalaVersion: String, isMain: Boolean) = {
    val base = if (jsJvmCrossProject) projectbase / ".." else projectbase
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, scalaMajor)) if scalaMajor >= 13 =>
        Seq(base / "compat" / "src" / (if (isMain) "main" else "test") / "scala-2.13")
          .map(_.getCanonicalFile)
      case Some((2, scalaMajor)) if scalaMajor >= 11 =>
        Seq(base / "compat" / "src" / (if (isMain) "main" else "test") / "scala-2.11")
          .map(_.getCanonicalFile)
      case _ => Nil
    }
  }

  val unmanagedMainDirsSetting = Seq(
    unmanagedSourceDirectories in Compile ++= {
      compatDirs(projectbase = baseDirectory.value,
                 scalaVersion = scalaVersion.value,
                 isMain = true)
    }
  )
  if (includeTestSrcs) {
    unmanagedMainDirsSetting ++ {
      unmanagedSourceDirectories in Test ++= {
        compatDirs(projectbase = baseDirectory.value,
                   scalaVersion = scalaVersion.value,
                   isMain = false)
      }
    }
  } else {
    unmanagedMainDirsSetting
  }
}

/**
  * Assumes that
  *
  *   - a corresponding directory exists under ./aggregates.
  *   - publishing 2.11.x, 2.12.x, 2.13.x
  */
def aggregateProject(id: String, projects: ProjectReference*): Project =
  Project(id = s"$id-aggregate", base = file(s"./aggregates/$id"))
    .settings(commonWithPublishSettings: _*)
    .settings(
      crossScalaVersions := Nil,
      crossVersion := CrossVersion.binary,
      // Do not publish the aggregate project (it just serves as an aggregate)
      libraryDependencies += {
        "org.scalatest" %% "scalatest" % scalaTestVersion % Test
      },
      publishArtifact := false,
      publishLocal := {}
    )
    .aggregate(projects: _*)
