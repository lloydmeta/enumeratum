package enumeratum

object ContextUtils {

  type Context = scala.reflect.macros.Context

  /**
   * Returns a TermName
   */
  def termName(c: Context)(name: String): c.universe.TermName = {
    c.universe.newTermName(name)
  }

  /**
   * Returns a companion symbol
   */
  def companion(c: Context)(sym: c.Symbol): c.universe.Symbol = sym.companionSymbol

}