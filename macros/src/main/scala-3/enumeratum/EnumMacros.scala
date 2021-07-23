package enumeratum

import scala.quoted._

object EnumMacros:

  inline def findValues[T]: IndexedSeq[T] = ${findValuesImpl[T]}

  private def findValuesImpl[T: Type](using Quotes): Expr[IndexedSeq[T]] = {
    import quotes.reflect._
    val tType = TypeRepr.of[T]
    validateType(tType)
    val selfType = This(Symbol.spliceOwner.owner.owner).tpe
    validateEnclosingModule(selfType)
    val subclassSymbols = enclosedSubClassTreesInModule(tType, selfType)
    val subclassExprs = subclassSymbols.map(v => Ref(v).asExprOf[T])
    '{ IndexedSeq[T](${Varargs(subclassExprs)}: _*) }
  }

  private def enclosedSubClassTreesInModule(using q: Quotes)(tType: q.reflect.TypeRepr, enclosingType: q.reflect.TypeRepr): List[q.reflect.Symbol] =
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

  private def isModule(using Quotes)(s: quotes.reflect.Symbol) =
    !s.moduleClass.isNoSymbol && !s.isType

  def validateType(using q: Quotes)(tType: q.reflect.TypeRepr) =
    if (!tType.typeSymbol.flags.is(q.reflect.Flags.Sealed))
      throw new IllegalArgumentException("You can only use findValues on sealed traits or classes")

  private def validateEnclosingModule(using q: Quotes)(tType: q.reflect.TypeRepr) =
    if (tType.typeSymbol.moduleClass.isNoSymbol) {
      throw new IllegalArgumentException("The enum (i.e. the class containing the case objects and the call to `findValues`) must be an object")
    }

