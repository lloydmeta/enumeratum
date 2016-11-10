package enumeratum

import scala.tools.reflect.ToolBox

/**
  * Eval with bits and pieces stolen from here and there...
  */
object Eval {

  def apply[A](
      string: String,
      compileOptions: String = s"-cp ${macroToolboxClassPath.mkString(";")}"
  ): A = {
    import scala.reflect.runtime.currentMirror
    val toolbox = currentMirror.mkToolBox(options = compileOptions)
    val tree    = toolbox.parse(string)
    toolbox.eval(tree).asInstanceOf[A]
  }

  def macroToolboxClassPath = {
    val paths = Seq(
      new java.io.File(s"macros/.jvm/target/scala-$scalaBinaryVersion/classes")
    )
    paths.foreach { p =>
      if (!p.exists) sys.error(s"output directory ${p.getAbsolutePath} does not exist.")
    }
    paths.map(_.getAbsolutePath)
  }

  def scalaBinaryVersion: String = {
    val PreReleasePattern = """.*-(M|RC).*""".r
    val Pattern           = """(\d+\.\d+)\..*""".r
    val SnapshotPattern   = """(\d+\.\d+\.\d+)-\d+-\d+-.*""".r
    scala.util.Properties.versionNumberString match {
      case s @ PreReleasePattern(_) => s
      case SnapshotPattern(v)       => v + "-SNAPSHOT"
      case Pattern(v)               => v
      case _                        => ""
    }
  }

}
