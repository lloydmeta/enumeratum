import com.typesafe.sbt.SbtGit.{GitKeys => git}

lazy val theVersion      = "1.5.3-SNAPSHOT"
lazy val theScalaVersion = "2.11.8"
/*
  2.12.0 support is currently defined as a separate project (scala_2_12) for convenience while
  integration libraries are still gaining 2.12.0 support
 */
lazy val scalaVersions = Seq("2.10.6", "2.11.8")

lazy val scalaTestVersion = "3.0.0"

// Library versions
lazy val reactiveMongoVersion = "0.12.0"
lazy val circeVersion         = "0.6.1"
lazy val uPickleVersion       = "0.4.4"
lazy val argonautVersion      = "6.2-RC2"
def thePlayVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 => "2.5.10"
    case Some((2, scalaMajor)) if scalaMajor == 10 => "2.4.8"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion")
  }

lazy val baseProjectRefs =
  Seq(macrosJs, macrosJvm, coreJs, coreJvm, coreJVMTests).map(Project.projectToRef)

lazy val integrationProjectRefs = Seq(
  enumeratumPlay,
  enumeratumPlayJson,
  enumeratumUPickleJs,
  enumeratumUPickleJvm,
  enumeratumCirceJs,
  enumeratumCirceJvm,
  enumeratumReactiveMongoBson,
  enumeratumArgonaut
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
      publishLocal := {}
    )
    .aggregate(baseProjectRefs ++ integrationProjectRefs: _*)

lazy val scala_2_12 = Project(id = "scala_2_12",
                              base = file("scala_2_12"),
                              settings = commonSettings ++ publishSettings)
  .settings(name := "enumeratum-scala_2_12",
            scalaVersion := "2.12.0", //not sure if this and below are needed
            crossScalaVersions := Seq("2.12.0"),
            crossVersion := CrossVersion.binary,
            // Do not publish this  project (it just serves as an aggregate)
            publishArtifact := false,
            publishLocal := {})
  .aggregate(
    baseProjectRefs ++
      Seq(
        enumeratumCirceJs,
        enumeratumCirceJvm,
        enumeratumUPickleJs,
        enumeratumUPickleJvm,
        enumeratumArgonaut
      ).map(Project.projectToRef): _*) // base plus known 2.12 friendly libs

lazy val core = crossProject
  .crossType(CrossType.Pure)
  .in(file("enumeratum-core"))
  .settings(
    name := "enumeratum"
  )
  .settings(testSettings: _*)
  .settings(commonWithPublishSettings: _*)
  .dependsOn(macros)
lazy val coreJs  = core.js
lazy val coreJvm = core.jvm

lazy val coreJVMTests = Project(id = "coreJVMTests",
                                base = file("enumeratum-core-jvm-tests"),
                                settings = commonWithPublishSettings)
  .settings(
    name := "coreJVMTests",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-compiler" % scalaVersion.value % Test
    ),
    publishArtifact := false
  )
  .settings(testSettings: _*)
  .dependsOn(coreJvm)

lazy val macros = crossProject
  .crossType(CrossType.Pure)
  .in(file("macros"))
  .settings(commonWithPublishSettings: _*)
  .settings(
    name := "enumeratum-macros",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    )
  )
  .settings(withCompatUnmanagedSources(jsJvmCrossProject = true,
                                       include_210Dir = true,
                                       includeTestSrcs = false): _*)
  .settings(testSettings: _*)
lazy val macrosJs  = macros.js
lazy val macrosJvm = macros.jvm

lazy val enumeratumReactiveMongoBson =
  Project(id = "enumeratum-reactivemongo-bson",
          base = file("enumeratum-reactivemongo-bson"),
          settings = commonWithPublishSettings)
    .settings(
      libraryDependencies ++= Seq(
        "org.reactivemongo" %% "reactivemongo" % reactiveMongoVersion
      )
    )
    .settings(testSettings: _*)
    .dependsOn(coreJvm % "test->test;compile->compile")

lazy val enumeratumPlayJson = Project(id = "enumeratum-play-json",
                                      base = file("enumeratum-play-json"),
                                      settings = commonWithPublishSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % thePlayVersion(scalaVersion.value)
    )
  )
  .settings(testSettings: _*)
  .dependsOn(coreJvm % "test->test;compile->compile")

lazy val enumeratumPlay = Project(id = "enumeratum-play",
                                  base = file("enumeratum-play"),
                                  settings = commonWithPublishSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % thePlayVersion(scalaVersion.value)
    )
  )
  .settings(testSettings: _*)
  .dependsOn(coreJvm, enumeratumPlayJson % "test->test;compile->compile")

lazy val enumeratumUPickle = crossProject
  .crossType(CrossType.Pure)
  .in(file("enumeratum-upickle"))
  .settings(commonWithPublishSettings: _*)
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
      Seq(impl.ScalaJSGroupID.withCross("com.lihaoyi", "upickle", cross) % uPickleVersion)
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
  .settings(testSettings: _*)
  .dependsOn(core % "test->test;compile->compile")
lazy val enumeratumUPickleJs  = enumeratumUPickle.js
lazy val enumeratumUPickleJvm = enumeratumUPickle.jvm

lazy val enumeratumCirce = crossProject
  .crossType(CrossType.Pure)
  .in(file("enumeratum-circe"))
  .settings(commonWithPublishSettings: _*)
  .settings(
    name := "enumeratum-circe",
    libraryDependencies ++= {
      import org.scalajs.sbtplugin._
      val cross = {
        if (ScalaJSPlugin.autoImport.jsDependencies.?.value.isDefined)
          ScalaJSCrossVersion.binary
        else
          CrossVersion.binary
      }
      Seq(impl.ScalaJSGroupID.withCross("io.circe", "circe-core", cross) % circeVersion)
    }
  )
  .settings(testSettings: _*)
  .dependsOn(core % "test->test;compile->compile")
lazy val enumeratumCirceJs  = enumeratumCirce.js
lazy val enumeratumCirceJvm = enumeratumCirce.jvm

lazy val enumeratumArgonaut =
  Project(id = "enumeratum-argonaut",
          base = file("enumeratum-argonaut"),
          settings = commonWithPublishSettings)
    .settings(
      libraryDependencies ++= Seq(
        "io.argonaut" %% "argonaut" % argonautVersion
      )
    )
    .settings(testSettings: _*)
    .dependsOn(coreJvm % "test->test;compile->compile")

lazy val commonSettings = Seq(
    organization := "com.beachape",
    version := theVersion,
    incOptions := incOptions.value.withLogRecompileOnMacro(false),
    scalaVersion := theScalaVersion,
    scalafmtConfig := Some(file(".scalafmt.conf"))
  ) ++
    reformatOnCompileSettings ++
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
  scalacOptions ++= {
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
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-dead-code", // N.B. doesn't work well with the ??? hole
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Xfuture"
    )
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) => base ++ Seq("-deprecation:false") // unused-import breaks Circe Either shim
      case Some((2, 11)) => base ++ Seq("-deprecation:false", "-Ywarn-unused-import")
      case _             => base
    }
  },
  wartremoverErrors in (Compile, compile) ++= Warts.unsafe.filterNot(_ == Wart.DefaultArguments)
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
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  }
)

val testSettings = {
  Seq(
    libraryDependencies += {
      import org.scalajs.sbtplugin._
      val crossVersion =
        if (ScalaJSPlugin.autoImport.jsDependencies.?.value.isDefined)
          ScalaJSCrossVersion.binary
        else
          CrossVersion.binary
      impl.ScalaJSGroupID
        .withCross("org.scalatest", "scalatest", crossVersion) % scalaTestVersion % Test
    },
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

scalafmtConfig := Some(file(".scalafmt.conf"))
