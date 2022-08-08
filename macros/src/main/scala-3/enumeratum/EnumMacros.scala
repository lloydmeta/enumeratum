package enumeratum

import scala.quoted.{Expr, Quotes, Type}

object EnumMacros:
  /** Finds any [A] in the current scope and returns an expression for a list of them. */
  def findValuesImpl[A](using tpe: Type[A], q: Quotes): Expr[IndexedSeq[A]] = {
    import q.reflect.*

    val repr       = validateType[A]
    val subclasses = enclosedSubClasses[A](q)(repr)

    buildSeqExpr[A](q)(subclasses)
  }

  /** Given an A, provides its companion. */
  def materializeEnumImpl[A, M: Type](using tpe: Type[A], q: Quotes): Expr[M] = {
    import q.reflect.*

    val repr = TypeRepr.of[A](using tpe)

    if (!repr.typeSymbol.companionModule.exists) {
      report.errorAndAbort(
        s"""
           |
           |  Could not find the companion object for type ${repr.typeSymbol}.
           |
           |  If you're sure the companion object exists, you might be able to fix this error by annotating the
           |  value you're trying to find the companion object for with a parent type (e.g. Light.Red: Light).
           |
           |  This error usually happens when trying to find the companion object of a hard-coded enum member, and
           |  is caused by Scala inferring the type to be the member's singleton type (e.g. Light.Red.type instead of
           |  Light).
           |
           |  To illustrate, given an enum:
           |
           |  sealed abstract class Light extends EnumEntry
           |  case object Light extends Enum[Light] {
           |    val values = findValues
           |    case object Red   extends Light
           |    case object Blue  extends Light
           |    case object Green extends Light
           |  }
           |
           |  and a method:
           |
           |  def indexOf[A <: EnumEntry: Enum](entry: A): Int = implicitly[Enum[A]].indexOf(entry)
           |
           |  Instead of calling like so: indexOf(Light.Red)
           |                Call like so: indexOf(Light.Red: Light)
         """.stripMargin
      )
    } else {
      Ref(repr.typeSymbol.companionModule).asExprOf[M]
    }
  }

  /** Makes sure that we can work with the given type as an enum:
    *
    * Aborts if the type is not sealed.
    */
  private[enumeratum] def validateType[T](using q: Quotes, tpe: Type[T]): q.reflect.TypeRepr = {
    import q.reflect.*

    val repr = TypeRepr.of[T](using tpe)

    if (!repr.classSymbol.exists(_.flags is Flags.Sealed)) {
      report.errorAndAbort(
        "You can only use findValues on sealed traits or classes"
      )
    }

    repr
  }

  /** Returns a sequence of symbols for objects that implement the given type
    *
    * @tparam the
    *   `Enum` type
    * @param tpr
    *   the representation of type `T` (also specified by `tpe`)
    */
  private[enumeratum] def enclosedSubClasses[T](q: Quotes)(
      tpr: q.reflect.TypeRepr
  )(using tpe: Type[T]): List[q.reflect.TypeRepr] = {
    import q.reflect.*

    given quotes: q.type = q

    @annotation.tailrec
    def isObject(sym: Symbol, ok: Boolean = false): Boolean = {
      if (!sym.flags.is(Flags.Module)) {
        val owner = sym.maybeOwner

        if (
          owner == defn.RootClass ||
          owner.flags.is(Flags.Package) ||
          owner.isAnonymousFunction
        ) {
          report.errorAndAbort(
            // Root must be a module to have same behaviour
            "The enum (i.e. the class containing the case objects and the call to `findValues`) must be an object"
          )
        }

        false
      } else {
        val owner = sym.maybeOwner

        if (!owner.exists || owner == defn.RootClass || owner.isTerm) {
          if (
            sym.flags.is(Flags.Module) && !sym.flags.is(Flags.Package) &&
            !sym.fullName.startsWith(tpr.typeSymbol.companionModule.fullName)
          ) {
            // See EnumSpec#'should return -1 for elements that do not exist'

            report.warning(
              s"The entry '${sym.fullName}' must be defined in the enum companion"
            )

            false
          } else {
            ok
          }
        } else {
          isObject(sym.maybeOwner, true)
        }
      }
    }

    type IsEntry[E <: T] = E

    @annotation.tailrec
    def subclasses(
        children: List[Tree],
        out: List[TypeRepr]
    ): List[TypeRepr] = {
      val childTpr = children.headOption.collect {
        case tpd: Typed =>
          tpd.tpt.tpe

        case vd: ValDef =>
          vd.tpt.tpe

        case cd: ClassDef =>
          cd.constructor.returnTpt.tpe

      }

      childTpr match {
        case Some(child) => {
          child.asType match {
            case '[IsEntry[t]] => {
              val tpeSym = child.typeSymbol
              // TODO: Check is subtype (same in Scala2?)

              if (!isObject(tpeSym)) {
                subclasses(children.tail, out)
              } else {
                subclasses(children.tail, child :: out)
              }
            }

            case _ =>
              subclasses(children.tail, out)
          }
        }

        case _ =>
          out.reverse
      }
    }

    tpr.classSymbol
      .flatMap { cls =>
        val types = subclasses(cls.children.map(_.tree), Nil)

        if (types.isEmpty) None else Some(types)
      }
      .getOrElse(List.empty[TypeRepr])
  }

  /** Builds and returns an expression for an `IndexedSeq`, containing singletons for the specified
    * subclasses.
    */
  private[enumeratum] def buildSeqExpr[T](q: Quotes)(
      subclasses: Seq[q.reflect.TypeRepr]
  )(using tpe: Type[T]): Expr[IndexedSeq[T]] = {
    given quotes: q.type = q
    import q.reflect.*

    if (subclasses.isEmpty) {
      '{ IndexedSeq.empty[T] }
    } else {
      val valueExprs = subclasses.map { sub =>
        Ref(sub.typeSymbol.companionModule).asExprOf[T]
      }

      val values = Expr.ofSeq(valueExprs)

      '{
        IndexedSeq[T](${ values }: _*)
      }
    }
  }

end EnumMacros
