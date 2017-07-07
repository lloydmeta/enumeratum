import com.typesafe.sbt.SbtGit.{GitKeys => git}

lazy val theScalaVersion = "2.11.11"

lazy val scalaVersions = Seq("2.10.6", "2.11.11", "2.12.2")

lazy val scalaTestVersion  = "3.0.1"
lazy val scalacheckVersion = "1.13.5"

// Library versions
lazy val reactiveMongoVersion = "0.12.1"
lazy val circeVersion         = "0.8.0"
lazy val uPickleVersion       = "0.4.4"
lazy val argonautVersion      = "6.2"
lazy val json4sVersion        = "3.5.1"

def thePlayVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 => "2.6.0"
    case Some((2, scalaMajor)) if scalaMajor == 10 => "2.4.11"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion")
  }
def thePlayJsonVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 => "2.6.0"
    case Some((2, scalaMajor)) if scalaMajor == 10 => "2.4.11"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion")
  }

def scalaTestPlay(scalaVersion: String) = CrossVersion.partialVersion(scalaVersion) match {
  case Some((2, scalaMajor)) if scalaMajor >= 11 =>
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0-M2" % Test
  case Some((2, scalaMajor)) if scalaMajor == 10 =>
    "org.scalatestplus" %% "play" % "1.4.0" % Test
  case _ =>
    throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion")
}

lazy val baseProjectRefs =
  Seq(macrosJS, macrosJVM, coreJS, coreJVM, coreJVMTests).map(Project.projectToRef)

lazy val integrationProjectRefs = Seq(
  enumeratumPlay,
  enumeratumPlayJson,
  enumeratumUPickleJs,
  enumeratumUPickleJvm,
  enumeratumCirceJs,
  enumeratumCirceJvm,
  enumeratumReactiveMongoBson,
  enumeratumArgonautJs,
  enumeratumArgonautJvm,
  enumeratumJson4s
).map(Project.projectToRef)

lazy val root =
  Project(id = "enumeratum-root", base = file("."), settings = commonWithPublishSettings)
    .settings(
      name := "enumeratum-root",
      crossScalaVersions := scalaVersions,
      crossVersion := CrossVersion.binary,
      git.gitRemoteRepo := "git@github.com:lloydmeta/enumeratum.git",
      // Do not publish the root project (it just serves as an aggregate)
      publishArtifact := false,
      publishLocal := {},
      aggregate in publish := false,
      aggregate in PgpKeys.publishSigned := false
    )
    .aggregate(baseProjectRefs ++ integrationProjectRefs: _*)

lazy val macrosAggregate = aggregateProject("macros", macrosJS, macrosJVM)
lazy val macros = crossProject
  .crossType(CrossType.Pure)
  .in(file("macros"))
  .settings(commonWithPublishSettings: _*)
  .settings(
    withCompatUnmanagedSources(jsJvmCrossProject = true,
                               include_210Dir = true,
                               includeTestSrcs = false): _*)
  .settings(
    name := "enumeratum-macros",
    version := Versions.Macros.head,
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    )
  )
  .settings(testSettings: _*)
lazy val macrosJS  = macros.js
lazy val macrosJVM = macros.jvm

// Aggregates core
lazy val coreAggregate = aggregateProject("core", coreJS, coreJVM)
lazy val core = crossProject
  .crossType(CrossType.Pure)
  .in(file("enumeratum-core"))
  .settings(
    name := "enumeratum",
    version := Versions.Core.head,
    libraryDependencies += "com.beachape" %% "enumeratum-macros" % Versions.Macros.stable
  )
  .settings(testSettings: _*)
  .settings(commonWithPublishSettings: _*)
//  .dependsOn(macros) // used for testing macros
lazy val coreJS  = core.js
lazy val coreJVM = core.jvm

lazy val testsAggregate = aggregateProject("test", enumeratumTestJs, enumeratumTestJvm)
// Project models used in test for some subprojects
lazy val enumeratumTest = crossProject
  .crossType(CrossType.Pure)
  .in(file("enumeratum-test"))
  .settings(testSettings: _*)
  .settings(commonWithPublishSettings: _*)
  .settings(
    name := "enumeratum-test",
    version := Versions.Core.stable,
    libraryDependencies += {
      import org.scalajs.sbtplugin._
      val crossVersion =
        if (ScalaJSPlugin.autoImport.jsDependencies.?.value.isDefined)
          ScalaJSCrossVersion.binary
        else
          CrossVersion.binary
      impl.ScalaJSGroupID
        .withCross("com.beachape", "enumeratum", crossVersion) % Versions.Core.stable
    }
  )
lazy val enumeratumTestJs  = enumeratumTest.js
lazy val enumeratumTestJvm = enumeratumTest.jvm

lazy val coreJVMTests = Project(id = "coreJVMTests",
                                base = file("enumeratum-core-jvm-tests"),
                                settings = commonWithPublishSettings)
  .settings(testSettings: _*)
  .settings(
    name := "coreJVMTests",
    version := Versions.Core.head,
    crossScalaVersions := scalaVersions,
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-compiler" % scalaVersion.value % Test
    ),
    publishArtifact := false
  )
  .dependsOn(coreJVM, macrosJVM)

lazy val enumeratumReactiveMongoBson =
  Project(id = "enumeratum-reactivemongo-bson",
          base = file("enumeratum-reactivemongo-bson"),
          settings = commonWithPublishSettings)
    .settings(testSettings: _*)
    .settings(
      crossScalaVersions := scalaVersions,
      version := "1.5.13-SNAPSHOT",
      libraryDependencies ++= Seq(
        "org.reactivemongo" %% "reactivemongo"   % reactiveMongoVersion,
        "com.beachape"      %% "enumeratum"      % Versions.Core.stable,
        "com.beachape"      %% "enumeratum-test" % Versions.Core.stable % Test
      )
    )

lazy val enumeratumPlayJson = Project(id = "enumeratum-play-json",
                                      base = file("enumeratum-play-json"),
                                      settings = commonWithPublishSettings)
  .settings(testSettings: _*)
  .settings(
    version := s"1.5.13-SNAPSHOT",
    crossScalaVersions := scalaVersions,
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json"       % thePlayJsonVersion(scalaVersion.value),
      "com.beachape"      %% "enumeratum"      % Versions.Core.stable,
      "com.beachape"      %% "enumeratum-test" % Versions.Core.stable % Test,
      "org.joda"          % "joda-convert"     % "1.8.1" % Provided
    )
  )

lazy val enumeratumPlay = Project(id = "enumeratum-play",
                                  base = file("enumeratum-play"),
                                  settings = commonWithPublishSettings)
  .settings(testSettings: _*)
  .settings(
    version := s"1.5.13-SNAPSHOT",
    crossScalaVersions := scalaVersions,
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play"            % thePlayVersion(scalaVersion.value),
      "com.beachape"      %% "enumeratum"      % Versions.Core.stable,
      "com.beachape"      %% "enumeratum-test" % Versions.Core.stable % Test,
      scalaTestPlay(scalaVersion.value)
    )
  )
  .dependsOn(enumeratumPlayJson % "test->test;compile->compile")

lazy val uPickleAggregate = aggregateProject("upickle", enumeratumUPickleJs, enumeratumUPickleJvm)
lazy val enumeratumUPickle = crossProject
  .crossType(CrossType.Pure)
  .in(file("enumeratum-upickle"))
  .settings(commonWithPublishSettings: _*)
  .settings(testSettings: _*)
  .settings(
    name := "enumeratum-upickle",
    version := "1.5.12-SNAPSHOT",
    libraryDependencies ++= {
      import org.scalajs.sbtplugin._
      val cross = {
        if (ScalaJSPlugin.autoImport.jsDependencies.?.value.isDefined)
          ScalaJSCrossVersion.binary
        else
          CrossVersion.binary
      }
      Seq(
        impl.ScalaJSGroupID.withCross("com.lihaoyi", "upickle", cross)     % uPickleVersion,
        impl.ScalaJSGroupID.withCross("com.beachape", "enumeratum", cross) % Versions.Core.stable
      )
    } ++ {
      val additionalMacroDeps =
        CrossVersion.partialVersion(scalaVersion.value) match {
          // if scala 2.11+ is used, quasiquotes are merged into scala-reflect
          case Some((2, scalaMajor)) if scalaMajor >= 11 =>
            Nil
          // in Scala 2.10, quasiquotes are provided by macro paradise
          case Some((2, 10)) =>
            Seq("org.scalamacros" %% "quasiquotes" % "2.0.1" cross CrossVersion.binary)
        }
      additionalMacroDeps
    }
  )
lazy val enumeratumUPickleJs  = enumeratumUPickle.js
lazy val enumeratumUPickleJvm = enumeratumUPickle.jvm

lazy val circeAggregate = aggregateProject("circe", enumeratumCirceJs, enumeratumCirceJvm)
lazy val enumeratumCirce = crossProject
  .crossType(CrossType.Pure)
  .in(file("enumeratum-circe"))
  .settings(commonWithPublishSettings: _*)
  .settings(testSettings: _*)
  .settings(
    name := "enumeratum-circe",
    version := "1.5.15-SNAPSHOT",
    libraryDependencies ++= {
      import org.scalajs.sbtplugin._
      val cross = {
        if (ScalaJSPlugin.autoImport.jsDependencies.?.value.isDefined)
          ScalaJSCrossVersion.binary
        else
          CrossVersion.binary
      }
      Seq(
        impl.ScalaJSGroupID.withCross("io.circe", "circe-core", cross)     % circeVersion,
        impl.ScalaJSGroupID.withCross("com.beachape", "enumeratum", cross) % Versions.Core.stable
      )
    }
  )
lazy val enumeratumCirceJs  = enumeratumCirce.js
lazy val enumeratumCirceJvm = enumeratumCirce.jvm

lazy val argonautAggregate =
  aggregateProject("argonaut", enumeratumArgonautJs, enumeratumArgonautJvm)
lazy val enumeratumArgonaut = crossProject
  .crossType(CrossType.Pure)
  .in(file("enumeratum-argonaut"))
  .settings(commonWithPublishSettings: _*)
  .settings(testSettings: _*)
  .settings(
    name := "enumeratum-argonaut",
    version := "1.5.12-SNAPSHOT",
    crossScalaVersions := scalaVersions,
    libraryDependencies ++= {
      import org.scalajs.sbtplugin._
      val cross = {
        if (ScalaJSPlugin.autoImport.jsDependencies.?.value.isDefined)
          ScalaJSCrossVersion.binary
        else
          CrossVersion.binary
      }
      Seq(
        impl.ScalaJSGroupID.withCross("io.argonaut", "argonaut", cross)    % argonautVersion,
        impl.ScalaJSGroupID.withCross("com.beachape", "enumeratum", cross) % Versions.Core.stable
      )
    }
  )

lazy val enumeratumArgonautJs  = enumeratumArgonaut.js
lazy val enumeratumArgonautJvm = enumeratumArgonaut.jvm

lazy val enumeratumJson4s =
  Project(id = "enumeratum-json4s",
          base = file("enumeratum-json4s"),
          settings = commonWithPublishSettings)
    .settings(testSettings: _*)
    .settings(
      version := "1.5.14-SNAPSHOT",
      crossScalaVersions := scalaVersions,
      libraryDependencies ++= Seq(
        "org.json4s"   %% "json4s-core"   % json4sVersion,
        "org.json4s"   %% "json4s-native" % json4sVersion % Test,
        "com.beachape" %% "enumeratum"    % Versions.Core.stable
      )
    )

lazy val commonSettings = Seq(
  organization := "com.beachape",
  incOptions := incOptions.value.withLogRecompileOnMacro(false),
  scalafmtOnCompile := true,
  scalaVersion := theScalaVersion
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
  wartremoverErrors in (Compile, compile) ++= Warts.unsafe
    .filterNot(_ == Wart.DefaultArguments) :+ Wart.ExplicitImplicitTypes,
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
      "-Yno-adapted-args",
      "-Ywarn-dead-code", // N.B. doesn't work well with the ??? hole
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Xfuture"
    )
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) =>
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
      <scm>
        <url>git@github.com:lloydmeta/enumeratum.git</url>
        <connection>scm:git:git@github.com:lloydmeta/enumeratum.git</connection>
      </scm>
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
      import org.scalajs.sbtplugin._
      val crossVersion =
        if (ScalaJSPlugin.autoImport.jsDependencies.?.value.isDefined)
          ScalaJSCrossVersion.binary
        else
          CrossVersion.binary
      Seq(
        impl.ScalaJSGroupID
          .withCross("org.scalatest", "scalatest", crossVersion) % scalaTestVersion % Test,
        impl.ScalaJSGroupID
          .withCross("org.scalacheck", "scalacheck", crossVersion) % scalacheckVersion % Test force ()
      )
    },
    doctestGenTests := {
      if (isScalaJSProject.value)
        Seq.empty
      else
        doctestGenTests.value
    },
    doctestTestFramework := DoctestTestFramework.ScalaTest,
    doctestWithDependencies := false,
    scalaJSStage in Test := FastOptStage
  )
}

lazy val benchmarking =
  Project(id = "benchmarking", base = file("benchmarking"), settings = commonWithPublishSettings)
    .settings(
      name := "benchmarking",
      crossScalaVersions := scalaVersions,
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
                               include_210Dir: Boolean,
                               includeTestSrcs: Boolean): Seq[Setting[_]] = {
  def compatDirs(projectbase: File, scalaVersion: String, isMain: Boolean) = {
    val base = if (jsJvmCrossProject) projectbase / ".." else projectbase
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, scalaMajor)) if scalaMajor >= 11 =>
        Seq(base / "compat" / "src" / (if (isMain) "main" else "test") / "scala-2.11")
          .map(_.getCanonicalFile)
      case Some((2, scalaMajor)) if scalaMajor == 10 && include_210Dir =>
        Seq(base / "compat" / "src" / (if (isMain) "main" else "test") / "scala-2.10")
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
  *   - publishing 2.10.x, 2.11.x, 2.12.x
  */
def aggregateProject(id: String, projects: ProjectReference*): Project =
  Project(id = s"$id-aggregate",
          base = file(s"./aggregates/$id"),
          settings = commonWithPublishSettings)
    .settings(
      crossScalaVersions := scalaVersions,
      crossVersion := CrossVersion.binary,
      // Do not publish the aggregate project (it just serves as an aggregate)
      publishArtifact := false,
      doctestWithDependencies := {
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, 12)) => false
          case _             => true
        }
      },
      publishLocal := {}
    )
    .aggregate(projects: _*)
