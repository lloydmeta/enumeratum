import com.typesafe.sbt.SbtGit.{GitKeys => git}
import sbtbuildinfo.BuildInfoPlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

lazy val scala_2_11Version = "2.11.12"
lazy val scala_2_12Version = "2.12.16"
lazy val scala_2_13Version = "2.13.8"
lazy val scala_3Version    = "3.2.1-RC1"
lazy val scalaVersionsAll =
  Seq(scala_2_11Version, scala_2_12Version, scala_2_13Version, scala_3Version)

lazy val theScalaVersion = scala_2_12Version

lazy val scalaTestVersion = "3.2.9"

// Library versions
lazy val reactiveMongoVersion = "1.1.0-RC6"
lazy val json4sVersion        = "4.0.3"
lazy val quillVersion         = "4.1.0"

def theDoobieVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor <= 11 => "0.7.1"
    case Some(_)                                   => "1.0.0-RC2"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion for Doobie")
  }

def theArgonautVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 => "6.2.5"
    case Some(_)                                   => "6.3.0"

    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion for Argonaut")
  }

def thePlayVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor >= 12 => "2.8.0"
    case Some((3, _))                              => "2.8.0"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion for Play")
  }

def theSlickVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor <= 11 => "3.3.3"
    case Some(_)                                   => "3.3.3"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion for Slick")
  }

def theCatsVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor <= 11 => "2.0.0"
    case Some(_)                                   => "2.6.1"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion for Cats")
  }

def thePlayJsonVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMajor)) if scalaMajor <= 11 => "2.7.3"
    // TODO drop 2.11 as play-json 2.7.x supporting Scala.js 1.x is unlikely?

    case Some(_) => "2.9.0"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion for play-json")
  }

def theCirceVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((3, _))                              => "0.14.1"
    case Some((2, scalaMajor)) if scalaMajor >= 12 => "0.14.1"
    case Some((2, scalaMajor)) if scalaMajor >= 11 => "0.11.1"
    case _ =>
      throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion")
  }

def theScalacheckVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, 11)) => "1.15.2"
    case _             => "1.15.4"
  }

def scalaTestPlay(scalaVersion: String) = CrossVersion.partialVersion(scalaVersion) match {
  case Some((2, scalaMajor)) if scalaMajor >= 12 =>
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
  case _ =>
    throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion for play-test")
}

lazy val baseProjectRefs =
  Seq(macrosJS, macrosJVM, coreJS, coreJVM, coreJVMTests).map(Project.projectToRef)

lazy val scala213ProjectRefs = Seq(
  enumeratumJson4s,
  enumeratumScalacheckJvm,
  enumeratumScalacheckJs,
  enumeratumPlayJsonJvm,
  enumeratumPlayJsonJs,
  enumeratumArgonautJs,
  enumeratumArgonautJvm,
  enumeratumSlick,
  enumeratumPlay,
  enumeratumCirceJvm,
  enumeratumCirceJs,
  enumeratumReactiveMongoBson,
  enumeratumCatsJvm,
  enumeratumCatsJs,
  enumeratumQuillJvm
  // enumeratumQuillJs  TODO re-enable once quill supports Scala.js 1.0
).map(Project.projectToRef)

lazy val scala_2_13 = Project(id = "scala_2_13", base = file("scala_2_13"))
  .settings(
    commonSettings ++ publishSettings,
    name               := "enumeratum-scala_2_13",
    scalaVersion       := scala_2_13Version, // not sure if this and below are needed
    crossScalaVersions := Seq(scala_2_13Version),
    crossVersion       := CrossVersion.binary,
    // Do not publish this  project (it just serves as an aggregate)
    publishArtifact := false,
    publishLocal    := {},
    // doctestWithDependencies := false, // sbt-doctest is not yet compatible with this 2.13
    publish / aggregate               := false,
    PgpKeys.publishSigned / aggregate := false
  )
  .aggregate((baseProjectRefs ++ scala213ProjectRefs): _*)

// Disable cats and circe js modules here, as they don't have versions compatible with Scala.js 1.0 on Scala 2.11
lazy val scala211ProjectRefs = Seq(
  enumeratumJson4s,
  enumeratumScalacheckJvm,
  enumeratumScalacheckJs,
  // enumeratumPlayJsonJvm,
  // TODO drop 2.11 as play-json 2.7.x supporting Scala.js 1.x is unlikely?
  // enumeratumPlayJsonJs, TODO re-enable once play-json supports Scala.js 1.0
  enumeratumArgonautJs,
  enumeratumArgonautJvm,
  enumeratumSlick,
  enumeratumPlay,
  enumeratumCirceJvm,
  enumeratumReactiveMongoBson,
  enumeratumCatsJvm,
  enumeratumQuillJvm
  // enumeratumQuillJs  TODO re-enable once quill supports Scala.js 1.0
).map(Project.projectToRef)

lazy val scala_2_11 = Project(id = "scala_2_11", base = file("scala_2_11"))
  .settings(
    commonSettings ++ publishSettings,
    name               := "enumeratum-scala_2_11",
    scalaVersion       := scala_2_11Version, // not sure if this and below are needed
    crossScalaVersions := Seq(scala_2_11Version),
    crossVersion       := CrossVersion.binary,
    // Do not publish this  project (it just serves as an aggregate)
    publishArtifact := false,
    publishLocal    := {},
    // doctestWithDependencies := false, // sbt-doctest is not yet compatible with this 2.13
    publish / aggregate               := false,
    PgpKeys.publishSigned / aggregate := false
  )
  .aggregate((baseProjectRefs ++ scala211ProjectRefs): _*)

lazy val integrationProjectRefs = Seq(
  enumeratumPlay,
  enumeratumPlayJsonJs,
  enumeratumPlayJsonJvm,
  enumeratumCirceJs,
  enumeratumCirceJvm,
  enumeratumReactiveMongoBson,
  enumeratumArgonautJs,
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
    .settings(commonWithPublishSettings)
    .settings(
      name               := "enumeratum-root",
      crossVersion       := CrossVersion.binary,
      crossScalaVersions := Nil,
      git.gitRemoteRepo  := "git@github.com:lloydmeta/enumeratum.git",
      // Do not publish the root project (it just serves as an aggregate)
      publishArtifact                   := false,
      publishLocal                      := {},
      publish / aggregate               := false,
      PgpKeys.publishSigned / aggregate := false
    )
    .aggregate(baseProjectRefs ++ integrationProjectRefs: _*)

lazy val macrosAggregate = aggregateProject("macros", macrosJS, macrosJVM)
lazy val macros = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("macros"))
  .settings(testSettings)
  .jsSettings(jsTestSettings)
  .settings(commonWithPublishSettings)
  .settings(withCompatUnmanagedSources(jsJvmCrossProject = true, includeTestSrcs = false))
  .settings(
    name    := "enumeratum-macros",
    version := Versions.Macros.head,
    crossScalaVersions := scalaVersionsAll, // eventually move this to aggregateProject once more 2.13 libs are out
    libraryDependencies += {
      if (scalaBinaryVersion.value == "3") {
        "org.scala-lang" %% "scala3-compiler" % scalaVersion.value % Provided
      } else {
        "org.scala-lang" % "scala-reflect" % scalaVersion.value
      }
    }
  )

lazy val macrosJS  = macros.js
lazy val macrosJVM = macros.jvm

lazy val useLocalVersion = sys.props.get("enumeratum.useLocalVersion").nonEmpty

// Aggregates core
lazy val coreAggregate = aggregateProject("core", coreJS, coreJVM)
lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-core"))
  .settings(testSettings)
  .jsSettings(jsTestSettings)
  .settings(commonWithPublishSettings)
  .settings(
    name               := "enumeratum",
    version            := Versions.Core.head,
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies ++= {
      if (useLocalVersion) {
        Seq.empty
      } else {
        Seq("com.beachape" %% "enumeratum-macros" % Versions.Macros.stable)
      }
    }
  )

def configureWithLocal(
    dep: (Project, Option[String]),
    deps: List[(Project, Option[String])] = Nil
): Project => Project = {
  if (useLocalVersion) { // used for testing macros
    { (prj: Project) =>
      (dep :: deps).foldLeft(prj) {
        case (p, (m, Some(x))) =>
          p.dependsOn(m % x)

        case (p, (m, None)) =>
          p.dependsOn(m)
      }
    }
  } else {
    identity[Project]
  }
}

def configureWithLocal(m: Project): Project => Project =
  configureWithLocal(m -> Option.empty[String])

def configureWithLocal(m: Project, x: String): Project => Project =
  configureWithLocal(m -> Some(x))

lazy val coreJS  = core.js.configure(configureWithLocal(macrosJS))
lazy val coreJVM = core.jvm.configure(configureWithLocal(macrosJVM))

lazy val coreJVMTests = Project(id = "coreJVMTests", base = file("enumeratum-core-jvm-tests"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      sbtVersion,
      BuildInfoKey.action("macrosJVMClassesDir") {
        (macrosJVM / Compile / classDirectory).value
      }
    ),
    buildInfoPackage := "enumeratum"
  )
  .settings(commonWithPublishSettings)
  .settings(testSettings)
  .settings(
    name               := "coreJVMTests",
    version            := Versions.Core.stable,
    crossScalaVersions := scalaVersionsAll,
    Test / sourceGenerators += CoreJVMTest.testsGenerator,
    libraryDependencies += {
      if (scalaBinaryVersion.value == "3") {
        "org.scala-lang" %% "scala3-compiler" % scalaVersion.value % Test
      } else {
        "org.scala-lang" % "scala-compiler" % scalaVersion.value % Test
      }
    },
    publishArtifact := false,
    publishLocal    := {}
  )
  .dependsOn(coreJVM, macrosJVM)

lazy val enumeratumReactiveMongoBson =
  Project(id = "enumeratum-reactivemongo-bson", base = file("enumeratum-reactivemongo-bson"))
    .settings(commonWithPublishSettings)
    .settings(testSettings)
    .settings(
      version            := "1.7.0",
      crossScalaVersions := scalaVersionsAll,
      libraryDependencies += {
        "org.reactivemongo" %% "reactivemongo-bson-api" % reactiveMongoVersion % Provided
      },
      libraryDependencies ++= {
        if (useLocalVersion) {
          Seq.empty
        } else {
          Seq(
            "com.beachape" %% "enumeratum"      % Versions.Core.stable,
            "com.beachape" %% "enumeratum-test" % Versions.Core.stable % Test
          )
        }
      }
    )
    .configure(configureWithLocal(coreJVM, "compile->compile;test->test"))

lazy val playJsonAggregate =
  aggregateProject("play-json", enumeratumPlayJsonJs, enumeratumPlayJsonJvm)

lazy val enumeratumPlayJson = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-play-json"))
  .settings(commonWithPublishSettings)
  .settings(testSettings)
  .jsSettings(jsTestSettings)
  .settings(
    name               := "enumeratum-play-json",
    version            := "1.7.1-SNAPSHOT",
    crossScalaVersions := Seq(scala_2_12Version, scala_2_13Version),
    libraryDependencies += {
      "com.typesafe.play" %%% "play-json" % thePlayJsonVersion(scalaVersion.value)
    },
    libraryDependencies ++= {
      if (useLocalVersion) {
        Seq.empty
      } else {
        Seq(
          "com.beachape" %% "enumeratum"      % Versions.Core.stable,
          "com.beachape" %% "enumeratum-test" % Versions.Core.stable % Test
        )
      }
    }
  )

lazy val enumeratumPlayJsonJs = enumeratumPlayJson.js
  .configure(configureWithLocal(coreJS, "compile->compile;test->test"))

lazy val enumeratumPlayJsonJvm = enumeratumPlayJson.jvm
  .configure(configureWithLocal(coreJVM, "compile->compile;test->test"))

lazy val enumeratumPlay = Project(id = "enumeratum-play", base = file("enumeratum-play"))
  .settings(commonWithPublishSettings)
  .settings(testSettings)
  .settings(
    version            := "1.7.1-SNAPSHOT",
    crossScalaVersions := Seq(scala_2_12Version, scala_2_13Version),
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % thePlayVersion(scalaVersion.value),
      scalaTestPlay(scalaVersion.value)
    ),
    libraryDependencies ++= {
      if (useLocalVersion) {
        Seq.empty
      } else {
        Seq(
          "com.beachape" %% "enumeratum"      % Versions.Core.stable,
          "com.beachape" %% "enumeratum-test" % Versions.Core.stable % Test
        )
      }
    }
  )
  .settings(withCompatUnmanagedSources(jsJvmCrossProject = false, includeTestSrcs = true))
  .configure(configureWithLocal(coreJVM, "compile->compile;test->test"))
  .dependsOn(enumeratumPlayJsonJvm % "compile->compile;test->test")

lazy val circeAggregate = aggregateProject("circe", enumeratumCirceJs, enumeratumCirceJvm)

lazy val enumeratumCirce = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-circe"))
  .settings(commonWithPublishSettings)
  .settings(testSettings)
  .jsSettings(jsTestSettings)
  .settings(
    name    := "enumeratum-circe",
    version := "1.7.1-SNAPSHOT",
    libraryDependencies += {
      "io.circe" %%% "circe-core" % theCirceVersion(scalaVersion.value)
    },
    libraryDependencies ++= {
      if (useLocalVersion) {
        Seq.empty
      } else {
        Seq("com.beachape" %%% "enumeratum" % Versions.Core.stable)
      }
    }
  )
  .jvmSettings(
    crossScalaVersions := scalaVersionsAll
  )
  .jsSettings(
    crossScalaVersions := Seq(scala_2_12Version, scala_2_13Version)
  )

lazy val enumeratumCirceJs = enumeratumCirce.js
  .configure(configureWithLocal(coreJS, "compile->compile;test->test"))

lazy val enumeratumCirceJvm = enumeratumCirce.jvm
  .configure(configureWithLocal(coreJVM, "compile->compile;test->test"))

lazy val argonautAggregate =
  aggregateProject("argonaut", enumeratumArgonautJs, enumeratumArgonautJvm)

lazy val enumeratumArgonaut = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-argonaut"))
  .settings(commonWithPublishSettings)
  .settings(testSettings)
  .jsSettings(jsTestSettings)
  .settings(
    name               := "enumeratum-argonaut",
    version            := "1.7.1-SNAPSHOT",
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies += {
      "io.argonaut" %%% "argonaut" % theArgonautVersion(scalaVersion.value)
    },
    libraryDependencies ++= {
      if (useLocalVersion) {
        Seq.empty
      } else {
        Seq("com.beachape" %%% "enumeratum" % Versions.Core.stable)
      }
    }
  )

lazy val enumeratumArgonautJs = enumeratumArgonaut.js
  .configure(configureWithLocal(coreJS))

lazy val enumeratumArgonautJvm = enumeratumArgonaut.jvm
  .configure(configureWithLocal(coreJVM))

lazy val enumeratumJson4s =
  Project(id = "enumeratum-json4s", base = file("enumeratum-json4s"))
    .settings(commonWithPublishSettings)
    .settings(testSettings)
    .settings(
      version            := "1.7.2-SNAPSHOT",
      crossScalaVersions := scalaVersionsAll,
      libraryDependencies ++= Seq(
        "org.json4s" %% "json4s-core"   % json4sVersion,
        "org.json4s" %% "json4s-native" % json4sVersion % Test
      ),
      libraryDependencies ++= {
        if (useLocalVersion) {
          Seq.empty
        } else {
          Seq("com.beachape" %%% "enumeratum" % Versions.Core.stable)
        }
      }
    )
    .configure(configureWithLocal(coreJVM))

lazy val scalacheckAggregate =
  aggregateProject("scalacheck", enumeratumScalacheckJs, enumeratumScalacheckJvm)

lazy val enumeratumScalacheck = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-scalacheck"))
  .settings(commonWithPublishSettings)
  .settings(testSettings)
  .jsSettings(jsTestSettings)
  .settings(
    name               := "enumeratum-scalacheck",
    version            := "1.7.1-SNAPSHOT",
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies ++= {
      Seq(
        "org.scalacheck"    %%% "scalacheck"      % theScalacheckVersion(scalaVersion.value),
        "org.scalatestplus" %%% "scalacheck-1-14" % "3.1.1.1" % Test
      )
    },
    libraryDependencies ++= {
      if (useLocalVersion) {
        Seq.empty
      } else {
        Seq(
          "com.beachape" %%% "enumeratum"      % Versions.Core.stable,
          "com.beachape" %%% "enumeratum-test" % Versions.Core.stable % Test
        )
      }
    }
  )

lazy val enumeratumScalacheckJs = enumeratumScalacheck.js
  .configure(configureWithLocal(coreJS, "compile->compile;test->test"))

lazy val enumeratumScalacheckJvm = enumeratumScalacheck.jvm
  .configure(configureWithLocal(coreJVM, "compile->compile;test->test"))

lazy val quillAggregate =
  aggregateProject(
    "quill", /*enumeratumQuillJs,*/ enumeratumQuillJvm
  ) // TODO re-enable once quill supports Scala.js 1.0
    .settings(crossScalaVersions := scalaVersionsAll)
lazy val enumeratumQuill =
  crossProject(JVMPlatform /*, JSPlatform TODO re-enable once quill supports Scala.js 1.0 */ )
    .crossType(CrossType.Pure)
    .in(file("enumeratum-quill"))
    .settings(commonWithPublishSettings)
    .settings(testSettings)
    // .jsSettings(jsTestSettings: _*) TODO re-enable once quill supports Scala.js 1.0 */,
    .settings(
      name               := "enumeratum-quill",
      version            := "1.7.2-SNAPSHOT",
      crossScalaVersions := scalaVersionsAll,
      libraryDependencies ++= {
        Seq(
          "io.getquill" %%% "quill-core" % quillVersion,
          "io.getquill" %%% "quill-sql"  % quillVersion % Test
        )
      },
      libraryDependencies ++= {
        if (useLocalVersion) {
          Seq.empty
        } else {
          Seq("com.beachape" %%% "enumeratum" % Versions.Core.stable)
        }
      },
      dependencyOverrides ++= {
        def pprintVersion(v: String) =
          if (v startsWith "2.11") "0.5.4" else "0.5.5"

        Seq(
          "com.lihaoyi" %%% "pprint" % pprintVersion(scalaVersion.value)
        )
      }
    )
// lazy val enumeratumQuillJs  = enumeratumQuill.js // TODO re-enable once quill supports Scala.js 1.0
lazy val enumeratumQuillJvm = enumeratumQuill.jvm.configure(configureWithLocal(coreJVM))

lazy val enumeratumDoobie =
  Project(id = "enumeratum-doobie", base = file("enumeratum-doobie"))
    .settings(commonWithPublishSettings)
    .settings(testSettings)
    .settings(
      crossScalaVersions := scalaVersionsAll,
      version            := "1.7.2-SNAPSHOT",
      libraryDependencies += {
        "org.tpolecat" %% "doobie-core" % theDoobieVersion(scalaVersion.value)
      },
      libraryDependencies ++= {
        if (useLocalVersion) {
          Seq.empty
        } else {
          Seq("com.beachape" %% "enumeratum" % Versions.Core.stable)
        }
      }
    )
    .configure(configureWithLocal(coreJVM))

lazy val enumeratumSlick =
  Project(id = "enumeratum-slick", base = file("enumeratum-slick"))
    .settings(commonWithPublishSettings)
    .settings(testSettings)
    .settings(
      version            := "1.7.1-SNAPSHOT",
      crossScalaVersions := scalaVersionsAll,
      libraryDependencies ++= Seq(
        "com.typesafe.slick" %% "slick" % theSlickVersion(scalaVersion.value),
        "com.h2database"      % "h2"    % "1.4.197" % Test
      ),
      libraryDependencies ++= {
        if (useLocalVersion) {
          Seq.empty
        } else {
          Seq("com.beachape" %% "enumeratum" % Versions.Core.stable)
        }
      }
    )
    .configure(configureWithLocal(coreJVM))

// Cats
lazy val catsAggregate = aggregateProject("cats", enumeratumCatsJs, enumeratumCatsJvm)

lazy val enumeratumCats = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-cats"))
  .settings(commonWithPublishSettings)
  .settings(testSettings)
  .jsSettings(jsTestSettings)
  .settings(
    name    := "enumeratum-cats",
    version := "1.7.1-SNAPSHOT",
    libraryDependencies += {
      "org.typelevel" %%% "cats-core" % theCatsVersion(scalaVersion.value)
    },
    libraryDependencies ++= {
      if (useLocalVersion) {
        Seq.empty
      } else {
        Seq("com.beachape" %%% "enumeratum" % Versions.Core.stable)
      }
    }
  )
  .jvmSettings(
    crossScalaVersions := scalaVersionsAll
  )
  .jsSettings(
    crossScalaVersions := Seq(scala_2_12Version, scala_2_13Version)
  )

lazy val enumeratumCatsJs = enumeratumCats.js.configure(configureWithLocal(coreJS))

lazy val enumeratumCatsJvm = enumeratumCats.jvm.configure(configureWithLocal(coreJVM))

lazy val commonSettings = Seq(
  organization       := "com.beachape",
  scalafmtOnCompile  := true,
  scalaVersion       := theScalaVersion,
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
  Global / transitiveClassifiers := Seq(Artifact.SourceClassifier)
)

lazy val compilerSettings = Seq(
  ThisBuild / scalaJSStage := {
    sys.props.get("sbt.scalajs.testOpt").orElse(sys.env.get("SCALAJS_TEST_OPT")) match {
      case Some("full") => FullOptStage
      case _            => FastOptStage
    }
  },
  Compile / compile / scalacOptions ++= {
    val minimal = Seq(
      "-encoding",
      "UTF-8", // yes, this is 2 args
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-unchecked",
      "-Xfatal-warnings"
    )

    val base = {
      if (scalaBinaryVersion.value == "3") {
        minimal
      } else {
        minimal ++ Seq(
          // "-Ywarn-adapted-args",
          "-Xlog-free-terms",
          "-Ywarn-dead-code", // N.B. doesn't work well with the ??? hole
          "-Ywarn-numeric-widen",
          "-Ywarn-value-discard",
          "-Xfuture"
        )
      }
    }

    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, m)) if m >= 13 =>
        base.filterNot(flag =>
          flag == "-Xfatal-warnings" || flag == "-Xfuture"
        ) ++ // todo see how to disable deprecations in 2.13.x
          Seq(
            /*"-deprecation:false", */ "-Xlint:-unused,_"
          ) // unused-import breaks Circe Either shim

      case Some((2, m)) if m >= 12 =>
        base ++ Seq(
          "-deprecation:false",
          "-Xlint:-unused,_"
        ) // unused-import breaks Circe Either shim

      case Some((2, 11)) => base ++ Seq("-deprecation:false", "-Xlint", "-Ywarn-unused-import")
      case Some((2, _))  => base ++ Seq("-Xlint")
      case _             => base
    }
  },
  Test / scalacOptions ++= {
    if (scalaBinaryVersion.value == "3") {
      Seq("-Yretain-trees")
    } else {
      Seq.empty
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
  pgpPassphrase          := sys.env.get("PGP_PASSPHRASE").map(_.toCharArray),
  publishMavenStyle      := true,
  Test / publishArtifact := false,
  PgpKeys.pgpPassphrase  := sys.env.get("PGP_PASSPHRASE").map(_.toCharArray),
  pomIncludeRepository := { _ =>
    false
  }
)

val testSettings = {
  Seq(
    libraryDependencies += {
      "org.scalatest" %%% "scalatest" % scalaTestVersion % Test
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
    coverageEnabled := false, // Disable until Scala.js 1.0 support is there https://github.com/scoverage/scalac-scoverage-plugin/pull/287
    doctestGenTests := {
      Seq.empty
    }
  )
}

lazy val benchmarking =
  Project(id = "benchmarking", base = file("benchmarking"))
    .settings(commonWithPublishSettings: _*)
    .settings(
      name         := "benchmarking",
      crossVersion := CrossVersion.binary,
      // Do not publish
      publishArtifact := false,
      publishLocal    := {}
    )
    .dependsOn((baseProjectRefs ++ integrationProjectRefs).map(ClasspathDependency(_, None)): _*)
    .enablePlugins(JmhPlugin)
    .settings(libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.21")

/** Helper function to add unmanaged source compat directories for different scala versions
  */
def withCompatUnmanagedSources(
    jsJvmCrossProject: Boolean,
    includeTestSrcs: Boolean
): Seq[Setting[_]] = {
  def compatDirs(projectbase: File, scalaVersion: String, isMain: Boolean) = {
    val base = if (jsJvmCrossProject) projectbase / ".." else projectbase
    val cat  = if (isMain) "main" else "test"

    CrossVersion.partialVersion(scalaVersion) match {
      case Some((3, _)) =>
        Seq(base / "compat" / "src" / cat / "scala-3")
          .map(_.getCanonicalFile)

      case Some((2, scalaMajor)) if scalaMajor >= 13 =>
        Seq(base / "compat" / "src" / cat / "scala-2.13")
          .map(_.getCanonicalFile)

      case Some((2, scalaMajor)) if scalaMajor >= 11 =>
        Seq(base / "compat" / "src" / cat / "scala-2.11")
          .map(_.getCanonicalFile)

      case _ => Nil
    }
  }

  val unmanagedMainDirsSetting = Seq(
    Compile / unmanagedSourceDirectories ++= {
      compatDirs(
        projectbase = baseDirectory.value,
        scalaVersion = scalaVersion.value,
        isMain = true
      )
    }
  )
  if (includeTestSrcs) {
    unmanagedMainDirsSetting ++ {
      Test / unmanagedSourceDirectories ++= {
        compatDirs(
          projectbase = baseDirectory.value,
          scalaVersion = scalaVersion.value,
          isMain = false
        )
      }
    }
  } else {
    unmanagedMainDirsSetting
  }
}

/** Assumes that
  *
  *   - a corresponding directory exists under ./aggregates.
  *   - publishing 2.11.x, 2.12.x, 2.13.x
  */
def aggregateProject(id: String, projects: ProjectReference*): Project =
  Project(id = s"$id-aggregate", base = file(s"./aggregates/$id"))
    .settings(commonWithPublishSettings: _*)
    .settings(
      crossScalaVersions := Nil,
      crossVersion       := CrossVersion.binary,
      // Do not publish the aggregate project (it just serves as an aggregate)
      libraryDependencies += {
        "org.scalatest" %% "scalatest" % scalaTestVersion % Test
      },
      publishArtifact := false,
      publishLocal    := {}
    )
    .aggregate(projects: _*)
