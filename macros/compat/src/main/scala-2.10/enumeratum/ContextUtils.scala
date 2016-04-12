package enumeratum

object ContextUtils {

  type Context = scala.reflect.macros.Context

  /**
   * Returns a TermName
   */
  def termName(c: Context)(name: String) = {
    c.universe.newTermName(name)
  }

}