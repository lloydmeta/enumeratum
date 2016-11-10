package enumeratum

object ContextUtils {

  type Context = scala.reflect.macros.blackbox.Context

  // Constant types
  type CTLong = Long
  type CTInt  = Int
  type CTChar = Char

  /**
    * Returns a TermName
    */
  def termName(c: Context)(name: String): c.universe.TermName = {
    c.universe.TermName(name)
  }

  /**
    * Returns a companion symbol
    */
  def companion(c: Context)(sym: c.Symbol): c.universe.Symbol = sym.companion

  /**
    * Returns a PartialFunction for turning symbols into names
    */
  def constructorsToParamNamesPF(
      c: Context
  ): PartialFunction[c.universe.Symbol, List[c.universe.Name]] = {
    case m if m.isConstructor =>
      m.asMethod.paramLists.flatten.map(_.asTerm.name)
  }
}
