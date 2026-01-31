package enumeratum

object ContextUtils {

  type Context = scala.reflect.macros.blackbox.Context

  // Constant types
  type CTLong = Long
  type CTInt  = Int
  type CTChar = Char

  /** Returns a TermName
    */
  def termName(c: Context)(name: String): c.universe.TermName = {
    c.universe.TermName(name)
  }

  /** Returns a companion symbol
    */
  def companion(c: Context)(sym: c.Symbol): c.universe.Symbol = sym.companion

  /** Returns the reserved constructor name
    */
  def constructorName(c: Context): c.universe.TermName = {
    c.universe.termNames.CONSTRUCTOR
  }

  /** Returns a named arg extractor
    */
  def namedArg(c: Context) = c.universe.AssignOrNamedArg
}
