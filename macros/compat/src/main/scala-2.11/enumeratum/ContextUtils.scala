package enumeratum

object ContextUtils {

  type Context = scala.reflect.macros.blackbox.Context

  /**
   * Returns a TermName
   */
  def termName(c: Context)(name: String): c.universe.TermName = {
    c.universe.TermName(name)
  }

}