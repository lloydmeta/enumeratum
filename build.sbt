import com.github.sbt.git.SbtGit.{GitKeys => git}
import sbtbuildinfo.BuildInfoPlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

lazy val scala_2_12Version = "2.12.21"
lazy val scala_2_13Version = "2.13.18"
lazy val scala_3Version    = "3.3.8"
lazy val scalaVersionsAll  = Seq(scala_2_12Version, scala_2_13Version, scala_3Version)

lazy val theScalaVersion = scala_2_12Version

lazy val scalaTestVersion = "3.2.19"

lazy val baseProjectRefs =
  Seq(macrosJS, macrosJVM, macrosNative, coreJS, coreJVM, coreNative, coreJVMTests).map(
    Project.projectToRef
  )

ThisBuild / version := Versions.Core.stable

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
  .aggregate((baseProjectRefs ++ scala213ProjectRefs)*)

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
    .aggregate((baseProjectRefs ++ integrationProjectRefs)*)

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
    version            := Versions.Macros.head,
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies ++= {
      if (useLocalVersion) {
        Seq.empty
      } else {
        Seq("com.beachape" %%% "enumeratum-macros" % Versions.Macros.head)
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

lazy val scalaXmlTest: ModuleID = "org.scala-lang.modules" %% "scala-xml" % Versions.Dependencies.scalaXml % Test

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
    version            := Versions.Macros.head,
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies += {
      "com.beachape" %%% "enumeratum" % Versions.Core.head
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
      version            := Versions.Macros.head,
      crossScalaVersions := scalaVersionsAll,
      libraryDependencies += {
        "org.reactivemongo" %% "reactivemongo-bson-api" % Versions.Dependencies.reactiveMongo % Provided
      },
      libraryDependencies += scalaXmlTest,
      libraryDependencies ++= {
        if (useLocalVersion) {
          Seq.empty
        } else {
          Seq(
            "com.beachape" %% "enumeratum"      % Versions.Core.head,
            "com.beachape" %% "enumeratum-test" % Versions.Core.head % Test
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
    name               := "enumeratum-play-json",
    version            := Versions.Macros.head,
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies ++= Seq(
      "org.playframework" %%% "play-json" % Versions.Dependencies.playJson,
      scalaXmlTest
    ),
    libraryDependencies ++= {
      if (useLocalVersion) {
        Seq.empty
      } else {
        Seq(
          "com.beachape" %% "enumeratum"      % Versions.Core.head,
          "com.beachape" %% "enumeratum-test" % Versions.Core.head % Test
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
    version := Versions.Macros.head,
    // Play do not support 2.12 (default from common settings)
    scalaVersion                                := scala_2_13Version,
    crossScalaVersions                          := Seq(scala_2_13Version, scala_3Version),
    libraryDependencies += ("org.playframework" %% "play" % Versions.Dependencies.play)
      .exclude("org.scala-lang.modules", "*"),
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % Versions.Dependencies.scalatestPlusPlay % Test,
    libraryDependencies ++= {
      if (useLocalVersion) {
        Seq.empty
      } else {
        Seq(
          "com.beachape" %% "enumeratum"      % Versions.Core.head,
          "com.beachape" %% "enumeratum-test" % Versions.Core.head % Test
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
    version := Versions.Macros.head,
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % Versions.Dependencies.circe,
      scalaXmlTest
    ),
    libraryDependencies ++= {
      if (useLocalVersion) {
        Seq.empty
      } else {
        Seq("com.beachape" %%% "enumeratum" % Versions.Core.head)
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
    version            := Versions.Macros.head,
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies ++= Seq(
      "io.argonaut" %%% "argonaut" % Versions.Dependencies.argonaut,
      scalaXmlTest
    ),
    libraryDependencies ++= {
      if (useLocalVersion) {
        Seq.empty
      } else {
        Seq("com.beachape" %%% "enumeratum" % Versions.Core.head)
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
      version            := Versions.Macros.head,
      crossScalaVersions := scalaVersionsAll,
      libraryDependencies ++= Seq(
        "io.github.json4s" %% "json4s-core"   % Versions.Dependencies.json4s,
        "io.github.json4s" %% "json4s-native" % Versions.Dependencies.json4s % Test,
        scalaXmlTest
      ),
      libraryDependencies ++= {
        if (useLocalVersion) {
          Seq.empty
        } else {
          Seq("com.beachape" %%% "enumeratum" % Versions.Core.head)
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
    version            := Versions.Macros.head,
    crossScalaVersions := scalaVersionsAll,
    libraryDependencies ++= Seq(
      "org.scalacheck"    %%% "scalacheck"        % Versions.Dependencies.scalacheck,
      "org.scalatestplus" %%% "scalacheck-1-18"   % Versions.Dependencies.scalacheckEffect % Test
    ).map(
      _.exclude("org.scala-lang.modules", "*")
        .exclude("org.scalatest", "*")
    ),
    libraryDependencies += scalaXmlTest,
    libraryDependencies ++= {
      if (useLocalVersion) {
        Seq.empty
      } else {
        Seq(
          "com.beachape" %%% "enumeratum"      % Versions.Core.head,
          "com.beachape" %%% "enumeratum-test" % Versions.Core.head % Test
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
  aggregateProject("quill", enumeratumQuillJvm)
    .settings(crossScalaVersions := scalaVersionsAll)

lazy val enumeratumQuill =
  crossProject(JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("enumeratum-quill"))
    .settings(commonWithPublishSettings)
    .settings(testSettings)
    .settings(
      name               := "enumeratum-quill",
      version            := Versions.Macros.head,
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
          Seq("com.beachape" %%% "enumeratum" % Versions.Core.head)
        }
      },
      dependencyOverrides += {
        val ver = scalaBinaryVersion.value match {
          case "3" => "0.7.3"
          case _   => "0.5.5"
        }

        "com.lihaoyi" %%% "pprint" % ver
      }
    )

lazy val enumeratumQuillJvm = enumeratumQuill.jvm.configure(configureWithLocal(coreJVM))

lazy val enumeratumDoobie =
  Project(id = "enumeratum-doobie", base = file("enumeratum-doobie"))
    .settings(commonWithPublishSettings)
    .settings(testSettings)
    .settings(
      crossScalaVersions                    := scalaVersionsAll,
      version                               := Versions.Macros.head,
      libraryDependencies += "org.typelevel" %% "doobie-core" % Versions.Dependencies.doobie,
      libraryDependencies += scalaXmlTest,
      libraryDependencies ++= {
        if (useLocalVersion) {
          Seq.empty
        } else {
          Seq("com.beachape" %% "enumeratum" % Versions.Core.head)
        }
      }
    )
    .configure(configureWithLocal(coreJVM))

lazy val enumeratumSlick =
  Project(id = "enumeratum-slick", base = file("enumeratum-slick"))
    .settings(commonWithPublishSettings)
    .settings(testSettings)
    .settings(
      version            := Versions.Macros.head,
      crossScalaVersions := scalaVersionsAll,
      libraryDependencies ++= Seq(
        ("com.typesafe.slick" %% "slick" % Versions.Dependencies.slick),
        "com.h2database"       % "h2"    % Versions.Dependencies.h2 % Test
      ),
      libraryDependencies += scalaXmlTest,
      libraryDependencies ++= {
        if (useLocalVersion) {
          Seq.empty
        } else {
          Seq("com.beachape" %% "enumeratum" % Versions.Core.head)
        }
      }
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
    name                                    := "enumeratum-cats",
    version                                 := Versions.Macros.head,
    libraryDependencies += "org.typelevel" %%% "cats-core" % Versions.Dependencies.cats,
    libraryDependencies += scalaXmlTest,
    libraryDependencies ++= {
      if (useLocalVersion) {
        Seq.empty
      } else {
        Seq("com.beachape" %%% "enumeratum" % Versions.Core.head)
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

lazy val localStagingResolver =
  "Local Sonatype staging manual" at "file://" + file("./target").absolutePath + "/sona-staging"

lazy val resolverSettings = Seq(
  resolvers ++= Seq(
    "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/",
    "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases",
    "Sonatype releases local" at "https://oss.sonatype.org/service/local/repositories/releases/content",
    localStagingResolver
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
    val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
    if (version.value.trim.endsWith("SNAPSHOT"))
      Some("central-snapshots" at centralSnapshots)
    else
      localStaging.value
  },
  publishMavenStyle      := true,
  Test / publishArtifact := false,
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
      libraryDependencies += "org.slf4j" % "slf4j-simple" % Versions.Dependencies.slf4j,
      // Do not publish
      publishArtifact := false,
      publishLocal    := {}
    )
    .dependsOn((baseProjectRefs ++ integrationProjectRefs).map(ClasspathDependency(_, None))*)
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
    .settings(commonWithPublishSettings*)
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
    .aggregate(projects*)
