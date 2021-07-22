package enumeratum

import scala.quoted._

object EnumMacros {

  inline def findValues[T]: IndexedSeq[T] = ${findValuesImpl[T]}

  private def findValuesImpl[T: Type](using Quotes): Expr[IndexedSeq[T]] = {
    import quotes.reflect._
    val tType = TypeRepr.of[T]
    val selfType = This(Symbol.spliceOwner.owner.owner).tpe
    val subclassSymbols = enclosedSubClassTreesInModule(tType, selfType)
    val subclassExprs = subclassSymbols.map(v => Ref(v).asExprOf[T])
    '{IndexedSeq[T](${Varargs(subclassExprs)}: _*)}
  }

  private def enclosedSubClassTreesInModule(using q: Quotes)(tType: q.reflect.TypeRepr, enclosingType: q.reflect.TypeRepr): List[q.reflect.Symbol] = {
    import q.reflect._
    enclosingType.typeSymbol.declaredFields.flatMap { member =>
      if (isModule(member)) {
        val memberType = enclosingType.memberType(member)
        if (memberType <:< tType) {
          member :: enclosedSubClassTreesInModule(tType, memberType)
        } else {
          enclosedSubClassTreesInModule(tType, memberType)
        }
      } else {
        Nil
      }
    }
  }

  private def isModule(using Quotes)(s: quotes.reflect.Symbol) =
    !s.moduleClass.isNoSymbol && !s.isType
}
