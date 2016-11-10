import sbt._
import Keys._

// Track https://github.com/sbt/sbt/issues/2786 or https://github.com/olafurpg/scalafmt/issues/485
// to know when we can remove this workaround
object MediatorWorkaround extends AutoPlugin {
  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements
  override def projectSettings =
    Seq(
      ivyScala := { ivyScala.value map {_.copy(overrideScalaVersion = sbtPlugin.value)} }
    )
}