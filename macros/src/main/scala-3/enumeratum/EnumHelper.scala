package test

import scala.deriving.Mirror
import scala.quoted.{Expr, Quotes, Type}
import scala.reflect.Enum

object EnumHelper:

  /** {{{
    * enum Color:
    *   case Red, Green, Blue
    *
    * import reactivemongo.api.bson.EnumHelper
    *
    * val valueOf: String => Option[Color] = EnumHelper.strictValueOf[Color]
    *
    * assert(valueOf("Red") contains Color.Red)
    * assert(valueOf("red").isEmpty)
    * }}}
    */
  inline def strictValueOf[T](using
      Mirror.SumOf[T]
  ): String => Option[T] = ${ strictValueOfImpl[T] }

  private def strictValueOfImpl[T](using
      Quotes,
      Type[T]
  ): Expr[String => Option[T]] = enumValueOfImpl(identity, None)

  private def enumValueOfImpl[T](
      labelNaming: String => String,
      normalize: Option[Expr[String => String]]
  )(using
      q: Quotes,
      tpe: Type[T]
  ): Expr[String => Option[T]] = {
    import q.reflect.*

    val tpr = TypeRepr.of(using tpe)

    val compSym = tpr.typeSymbol.companionModule

    if (compSym == Symbol.noSymbol) {
      report.errorAndAbort(s"Unresolved type: ${tpr.typeSymbol.fullName}")
    }

    val compRef = Ref(compSym)

    val cases = compSym.fieldMembers
      .flatMap { fieldSym =>
        val fieldTerm = compRef.select(fieldSym)

        if (fieldTerm.tpe <:< tpr) {
          Seq(fieldSym -> fieldTerm.asExprOf[T])
        } else {
          Seq.empty[(Symbol, Expr[T])]
        }
      }
      .zipWithIndex
      .map { case ((sym, expr), i) =>
        val name                = sym.name.toLowerCase
        val body: Expr[Some[T]] = '{ Some(${ expr }) }

        CaseDef(
          Literal(StringConstant(labelNaming(sym.name))),
          guard = None,
          rhs = body.asTerm
        )
      }

    val none = CaseDef(
      Wildcard(),
      None,
      '{ Option.empty[T] }.asTerm
    )

    def mtch(s: Expr[String]): Expr[Option[T]] = {
      Match(s.asTerm, cases :+ none).asExprOf[Option[T]]
    }

    normalize match {
      case Some(nz) =>
        '{ (s: String) =>
          val in = ${ nz }(s)
          ${ mtch('in) }
        }

      case _ =>
        '{ (s: String) => ${ mtch('s) } }
    }
  }
end EnumHelper
