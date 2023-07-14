import com.typesafe.sbt.SbtGit.{GitKeys => git}
import sbtbuildinfo.BuildInfoPlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

lazy val scala_2_12Version = "2.12.17"
lazy val scala_2_13Version = "2.13.10"
lazy val scala_3Version    = "3.2.1"
lazy val scalaVersionsAll = Seq(scala_2_12Version, scala_2_13Version, scala_3Version)

lazy val theScalaVersion = scala_2_12Version

lazy val scalaTestVersion = "3.2.14"

def scalaTestPlay(scalaVersion: String) = CrossVersion.partialVersion(scalaVersion) match {
  case Some((2, scalaMajor)) if scalaMajor >= 12 =>
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

  case Some((3, _)) =>
    ("org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test)
      .cross(CrossVersion.for3Use2_13)
      .exclude("org.scalactic", "*")
      .exclude("org.scalatest", "*")
      .exclude("org.scala-lang.modules", "*")
      .exclude("com.typesafe.play", "play-json_2.13")

  case _ =>
    throw new IllegalArgumentException(s"Unsupported Scala version $scalaVersion for play-test")
}

lazy val baseProjectRefs =
  Seq(macrosJS, macrosJVM, macrosNative, coreJS, coreJVM, coreNative, coreJVMTests).map(
    Project.projectToRef
  )

lazy val scala213ProjectRefs = Seq(
  enumeratumJson4s,
  enumeratumScalacheckJvm,
  enumeratumScalacheckJs,
  enumeratumScalacheckNative,
  enumeratumPlayJsonJvm,
  enumeratumPlayJsonJs,
  enumeratumArgonautJs,
  enumeratumArgonautJvm,
  enumeratumSlick,
  enumeratumPlay,
  enumeratumCirceJvm,
  enumeratumCirceJs,
  enumeratumCirceNative,
  enumeratumReactiveMongoBson,
  enumeratumCatsJvm,
  enumeratumCatsJs,
  enumeratumCatsNative,
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

lazy val integrationProjectRefs = Seq(
  enumeratumPlay,
  enumeratumPlayJsonJs,
  enumeratumPlayJsonJvm,
  enumeratumCirceJs,
  enumeratumCirceJvm,
  enumeratumCirceNative,
  enumeratumReactiveMongoBson,
  enumeratumArgonautJs,
  enumeratumArgonautJvm,
  enumeratumJson4s,
  enumeratumScalacheckJs,
  enumeratumScalacheckJvm,
  enumeratumScalacheckNative,
//  enumeratumQuillJs, TODO re-enable once quill supports Scala.js 1.0
  enumeratumQuillJvm,
  enumeratumDoobie,
  enumeratumSlick,
  enumeratumCatsJs,
  enumeratumCatsJvm,
  enumeratumCatsNative
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

lazy val macrosAggregate = aggregateProject("macros", macrosJS, macrosJVM, macrosNative)
lazy val macros = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("macros"))
  .settings(testSettings)
  .jsSettings(jsTestSettings)
  .nativeSettings(nativeTestSettings)
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
    },
    libraryDependencies += scalaXmlTest
  )

lazy val macrosJS     = macros.js
lazy val macrosJVM    = macros.jvm
lazy val macrosNative = macros.native

lazy val useLocalVersion = sys.props.get("enumeratum.useLocalVersion").nonEmpty

// Aggregates core
lazy val coreAggregate = aggregateProject("core", coreJS, coreJVM, coreNative)
lazy val core = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-core"))
  .settings(testSettings)
  .jsSettings(jsTestSettings)
  .nativeSettings(nativeTestSettings)
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
    },
    libraryDependencies += scalaXmlTest
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

lazy val coreJS     = core.js.configure(configureWithLocal(macrosJS))
lazy val coreJVM    = core.jvm.configure(configureWithLocal(macrosJVM))
lazy val coreNative = core.native.configure(configureWithLocal(macrosNative))

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
    version            := Versions.Core.head,
    crossScalaVersions := scalaVersionsAll,
    Test / sourceGenerators += CoreJVMTest.testsGenerator,
    libraryDependencies += {
      if (scalaBinaryVersion.value == "3") {
        "org.scala-lang" %% "scala3-compiler" % scalaVersion.value % Test
      } else {
        "org.scala-lang" % "scala-compiler" % scalaVersion.value % Test
      }
    },
    libraryDependencies += scalaXmlTest,
    publishArtifact := false,
    publishLocal    := {}
  )
  .dependsOn(coreJVM, macrosJVM)

lazy val scalaXmlTest: ModuleID = "org.scala-lang.modules" %% "scala-xml" % "2.1.0" % Test

lazy val testsAggregate =
  aggregateProject("test", enumeratumTestJs, enumeratumTestJvm, enumeratumTestNative)
// Project models used in test for some subprojects
lazy val enumeratumTest = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-test"))
  .settings(testSettings)
  .jsSettings(jsTestSettings)
  .nativeSettings(nativeTestSettings)
  .settings(commonWithPublishSettings)
  .settings(
    name               := "enumeratum-test",
    version            := Versions.Core.head,
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies += {
      "com.beachape" %%% "enumeratum" % Versions.Core.stable
    }
  )
lazy val enumeratumTestJs     = enumeratumTest.js
lazy val enumeratumTestJvm    = enumeratumTest.jvm
lazy val enumeratumTestNative = enumeratumTest.native

lazy val enumeratumReactiveMongoBson =
  Project(id = "enumeratum-reactivemongo-bson", base = file("enumeratum-reactivemongo-bson"))
    .settings(commonWithPublishSettings)
    .settings(testSettings)
    .settings(
      version            := Versions.Core.head,
      crossScalaVersions := scalaVersionsAll,
      libraryDependencies += {
        "org.reactivemongo" %% "reactivemongo-bson-api" % "1.1.0-RC9" % Provided
      },
      libraryDependencies += scalaXmlTest,
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

// Play-JSON
lazy val playJsonAggregate =
  aggregateProject("play-json", enumeratumPlayJsonJs, enumeratumPlayJsonJvm)

lazy val enumeratumPlayJson = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-play-json"))
  .settings(commonWithPublishSettings)
  .settings(testSettings)
  .jsSettings(jsTestSettings)
  .settings(
    name    := "enumeratum-play-json",
    version := Versions.Core.head,
    crossScalaVersions := Seq(
      scala_2_12Version,
      scala_2_13Version,
      scala_3Version
    ),
    libraryDependencies ++= Seq(
      "com.typesafe.play" %%% "play-json" % "2.10.0-RC6",
      scalaXmlTest
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

lazy val enumeratumPlayJsonJs = enumeratumPlayJson.js
  .configure(configureWithLocal(coreJS, "compile->compile;test->test"))

lazy val enumeratumPlayJsonJvm = enumeratumPlayJson.jvm
  .configure(configureWithLocal(coreJVM, "compile->compile;test->test"))

// Play
lazy val enumeratumPlay = Project(id = "enumeratum-play", base = file("enumeratum-play"))
  .settings(commonWithPublishSettings)
  .settings(testSettings)
  .settings(
    version            := Versions.Core.head,
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies += {
      val dep = ("com.typesafe.play" %% "play" % "2.8.0").exclude("org.scala-lang.modules", "*")

      if (scalaBinaryVersion.value == "3") {
        dep
          .exclude("org.scala-lang.modules", "*")
          .exclude("com.typesafe.play", "play-json_2.13")
          .cross(CrossVersion.for3Use2_13)
      } else {
        dep
      }
    },
    libraryDependencies += scalaTestPlay(scalaVersion.value),
    libraryDependencies ++= {
      if (useLocalVersion) {
        Seq.empty
      } else {
        Seq(
          "com.beachape" %% "enumeratum"      % Versions.Core.stable,
          "com.beachape" %% "enumeratum-test" % Versions.Core.stable % Test
        )
      }
    },
    scalacOptions ++= {
      if (scalaBinaryVersion.value == "3") {
        Seq("-Wconf:cat=deprecation&msg=.*right-biased.*:s")
      } else {
        Seq.empty
      }
    }
  )
  .settings(withCompatUnmanagedSources(jsJvmCrossProject = false, includeTestSrcs = true))
  .configure(configureWithLocal(coreJVM, "compile->compile;test->test"))
  .dependsOn(enumeratumPlayJsonJvm % "compile->compile;test->test")

// Circe
lazy val circeAggregate =
  aggregateProject("circe", enumeratumCirceJs, enumeratumCirceJvm, enumeratumCirceNative)

lazy val enumeratumCirce = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-circe"))
  .settings(commonWithPublishSettings)
  .settings(testSettings)
  .jsSettings(jsTestSettings)
  .nativeSettings(nativeTestSettings)
  .settings(
    name    := "enumeratum-circe",
    version := Versions.Core.head,
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % "0.14.3",
      scalaXmlTest
    ),
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
    crossScalaVersions := Seq(scala_2_12Version, scala_2_13Version, scala_3Version)
  )
  .nativeSettings(
    crossScalaVersions := Seq(scala_2_12Version, scala_2_13Version, scala_3Version)
  )
lazy val enumeratumCirceJs = enumeratumCirce.js
  .configure(configureWithLocal(coreJS, "compile->compile;test->test"))

lazy val enumeratumCirceJvm = enumeratumCirce.jvm
  .configure(configureWithLocal(coreJVM, "compile->compile;test->test"))

lazy val enumeratumCirceNative = enumeratumCirce.native
  .configure(configureWithLocal(coreNative, "compile->compile;test->test"))

// Argonaut
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
    version            := Versions.Core.head,
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies ++= {
      val ver: String = {
        if (scalaBinaryVersion.value == "3") {
          "6.3.8"
        } else {
          "6.2.5"
        }
      }

      Seq(
        "io.argonaut" %%% "argonaut" % ver,
        scalaXmlTest
      )
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

// JSON4S
lazy val enumeratumJson4s =
  Project(id = "enumeratum-json4s", base = file("enumeratum-json4s"))
    .settings(commonWithPublishSettings)
    .settings(testSettings)
    .settings(
      version            := Versions.Core.head,
      crossScalaVersions := scalaVersionsAll,
      libraryDependencies ++= {
        val ver = "4.0.3"

        Seq(
          "org.json4s" %% "json4s-core"   % ver,
          "org.json4s" %% "json4s-native" % ver % Test,
          scalaXmlTest
        )
      },
      libraryDependencies ++= {
        if (useLocalVersion) {
          Seq.empty
        } else {
          Seq("com.beachape" %%% "enumeratum" % Versions.Core.stable)
        }
      }
    )
    .settings(
      // TODO: Remove once JSON4S is fixed for Scala3;
      // https://github.com/json4s/json4s/issues/1035
      disabledSettings
    )
    .configure(configureWithLocal(coreJVM))

// ScalaCheck
lazy val scalacheckAggregate =
  aggregateProject(
    "scalacheck",
    enumeratumScalacheckJs,
    enumeratumScalacheckJvm,
    enumeratumScalacheckNative
  )

lazy val enumeratumScalacheck = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-scalacheck"))
  .settings(commonWithPublishSettings)
  .settings(testSettings)
  .jsSettings(jsTestSettings)
  .nativeSettings(nativeTestSettings)
  .settings(
    name               := "enumeratum-scalacheck",
    version            := Versions.Core.head,
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies ++= {
      val (ver, mod, ver2) = ("1.17.0", "scalacheck-1-17", "3.2.14.0")

      Seq(
        "org.scalacheck"    %%% "scalacheck" % ver,
        "org.scalatestplus" %%% mod          % ver2 % Test
      ).map(
        _.exclude("org.scala-lang.modules", "*")
          .exclude("org.scalatest", "*")
      )
    },
    libraryDependencies += scalaXmlTest,
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
  .jvmSettings(
    crossScalaVersions := scalaVersionsAll
  )
  .nativeSettings(
    crossScalaVersions := scalaVersionsAll
  )

lazy val enumeratumScalacheckJs = enumeratumScalacheck.js
  .configure(configureWithLocal(coreJS, "compile->compile;test->test"))

lazy val enumeratumScalacheckJvm = enumeratumScalacheck.jvm
  .configure(configureWithLocal(coreJVM, "compile->compile;test->test"))

lazy val enumeratumScalacheckNative = enumeratumScalacheck.native
  .configure(configureWithLocal(coreNative, "compile->compile;test->test"))

// Quill
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
      version            := Versions.Core.head,
      crossScalaVersions := scalaVersionsAll,
      libraryDependencies ++= {
        val (core, ver) = {
          if (scalaBinaryVersion.value == "3") {
            "quill-engine" -> "4.4.0"
          } else {
            "quill-core" -> "4.1.0"
          }
        }

        Seq(
          "io.getquill" %%% core        % ver,
          "io.getquill" %%% "quill-sql" % ver % Test,
          scalaXmlTest
        )
      },
      libraryDependencies ++= {
        if (useLocalVersion) {
          Seq.empty
        } else {
          Seq("com.beachape" %%% "enumeratum" % Versions.Core.stable)
        }
      },
      dependencyOverrides += {
        val ver = scalaBinaryVersion.value match {
          case "3"    => "0.7.3"
          case _      => "0.5.5"
        }

        "com.lihaoyi" %%% "pprint" % ver
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
      version            := "1.7.4-SNAPSHOT",
      libraryDependencies += "org.tpolecat" %% "doobie-core" % "1.0.0-RC2",
      libraryDependencies += scalaXmlTest,
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
      version            := "1.7.4-SNAPSHOT",
      crossScalaVersions := scalaVersionsAll,
      libraryDependencies ++= Seq(
        ("com.typesafe.slick" %% "slick" % "3.4.1").cross(CrossVersion.for3Use2_13),
        "com.h2database"       % "h2"    % "1.4.197" % Test
      ),
      libraryDependencies ++= {
        if (useLocalVersion) {
          Seq.empty
        } else {
          Seq("com.beachape" %% "enumeratum" % Versions.Core.stable)
        }
      }
    )
    .settings( // TODO: Remove once Slick is published for Dotty
      disabledSettings
    )
    .configure(configureWithLocal(coreJVM))

// Cats
lazy val catsAggregate =
  aggregateProject("cats", enumeratumCatsJs, enumeratumCatsJvm, enumeratumCatsNative)

lazy val enumeratumCats = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("enumeratum-cats"))
  .settings(commonWithPublishSettings)
  .settings(testSettings)
  .jsSettings(jsTestSettings)
  .nativeSettings(nativeTestSettings)
  .settings(
    name    := "enumeratum-cats",
    version := Versions.Core.head,
    libraryDependencies += "org.typelevel" %%% "cats-core" % "2.9.0",
    libraryDependencies += scalaXmlTest,
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
    crossScalaVersions := scalaVersionsAll
  )
  .nativeSettings(
    crossScalaVersions := scalaVersionsAll
  )

lazy val enumeratumCatsJs = enumeratumCats.js.configure(configureWithLocal(coreJS))

lazy val enumeratumCatsJvm = enumeratumCats.jvm.configure(configureWithLocal(coreJVM))

lazy val enumeratumCatsNative = enumeratumCats.native.configure(configureWithLocal(coreNative))

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
    "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases",
    "Sonatype releases local" at "https://oss.sonatype.org/service/local/repositories/releases/content"
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
        minimal :+ "-deprecation"
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
      val dep = "org.scalatest" %%% "scalatest" % scalaTestVersion % Test

      if (scalaBinaryVersion.value == "3") {
        dep.exclude("org.scala-lang.modules", "*")
      } else {
        dep
      }
    },
    doctestGenTests := {
      val originalValue = doctestGenTests.value
      Seq.empty // TODO: re-enable originalValue
    },
    doctestTestFramework := DoctestTestFramework.ScalaTest
  )
}

val jsTestSettings = Seq(
  coverageEnabled := false, // Disable until Scala.js 1.0 support is there https://github.com/scoverage/scalac-scoverage-plugin/pull/287
  doctestGenTests := Seq.empty
)

val nativeTestSettings = Seq(
  coverageEnabled := false,
  doctestGenTests := Seq.empty
)

lazy val disabledSettings = Seq(
  doctestScalaTestVersion := Some(scalaTestVersion),
  sourceDirectory := {
    if (scalaBinaryVersion.value == "3") new java.io.File("/no/sources")
    else sourceDirectory.value
  },
  publishArtifact := (scalaBinaryVersion.value != "3"),
  publishLocal := {
    if (publishArtifact.value) ({})
    else publishLocal.value
  }
)

lazy val benchmarking =
  Project(id = "benchmarking", base = file("benchmarking"))
    .settings(commonWithPublishSettings)
    .settings(
      name                              := "benchmarking",
      crossVersion                      := CrossVersion.binary,
      libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.21",
      // Do not publish
      publishArtifact := false,
      publishLocal    := {}
    )
    .dependsOn((baseProjectRefs ++ integrationProjectRefs).map(ClasspathDependency(_, None)): _*)
    .enablePlugins(JmhPlugin)

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

      case Some((2, 13)) =>
        Seq(base / "compat" / "src" / cat / "scala-2.13")
          .map(_.getCanonicalFile)

      case Some((2, 12)) =>
        Seq(base / "compat" / "src" / cat / "scala-2.12")
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
  *   - publishing 2.12.x, 2.13.x, 3.x
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
