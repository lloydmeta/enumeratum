package enumeratum

import scala.tools.reflect.ToolBox

/** Eval with bits and pieces stolen from here and there...
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
      BuildInfo.macrosJVMClassesDir
    )
    paths.map(_.getAbsolutePath)
  }
}
