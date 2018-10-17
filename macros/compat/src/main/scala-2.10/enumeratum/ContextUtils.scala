package enumeratum

object ContextUtils {

  type Context = scala.reflect.macros.Context

  // In 2.10, the constants have Java boxed types at compile time for some reason
  type CTLong = java.lang.Long
  type CTInt  = java.lang.Integer
  type CTChar = java.lang.Character

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

  /**
    * Returns a PartialFunction for turning symbols into names
    */
  def constructorsToParamNamesPF(
      c: Context
  ): PartialFunction[c.universe.Symbol, List[c.universe.Name]] = {
    case m if m.isMethod && m.asMethod.isConstructor =>
      m.asMethod.paramss.flatten.map(_.asTerm.name)
  }

  /**
    * Returns the reserved constructor name
    */
  def constructorName(c: Context): c.universe.TermName = {
    c.universe.nme.CONSTRUCTOR
  }

  /**
    * Returns a named arg extractor
    */
  def namedArg(c:Context) = c.universe.AssignOrNamedArg
}
