package enumeratum

import scala.quoted.{Expr, Quotes, Type}

object EnumMacros:
  /** Finds any [A] in the current scope and returns an expression for a list of them. */
  def findValuesImpl[A](using tpe: Type[A], q: Quotes): Expr[IndexedSeq[A]] = {
    import q.reflect.*

    val definingTpeSym = Symbol.spliceOwner.maybeOwner.maybeOwner

    if (!definingTpeSym.flags.is(Flags.Module)) {
      // The enum itself is not an object (it's a class or something else) - this is an error
      report.errorAndAbort(
        // Root must be a module to have same behaviour
        s"The enum (i.e. the class containing the case objects and the call to `findValues`) must be an object: ${definingTpeSym.fullName}"
      )
    } else {
      // The enum object (definingTpeSym) is a module, but check if it's nested inside a class/trait
      val ownerOfEnum = definingTpeSym.maybeOwner

      if (
        ownerOfEnum.exists && ownerOfEnum.isClassDef && !ownerOfEnum.flags
          .is(Flags.Module) && !ownerOfEnum.flags.is(Flags.Package)
      ) {
        val ownerKind = if (ownerOfEnum.flags.is(Flags.Trait)) "trait" else "class"
        report.warning(
          s"""Enum object '${definingTpeSym.name}' is nested inside a $ownerKind '${ownerOfEnum.fullName}'.
             |
             |This pattern is problematic and will not work correctly:
             |1. Each instance of the $ownerKind has its own copy of the enum, which is likely not what you want
             |2. In Scala 3, findValues cannot discover members in this context (it will return an empty collection)
             |3. The question of identity becomes unclear: should enum members from different $ownerKind instances be equal?
             |
             |Consider moving your enum to:
             |  - A top-level object
             |  - Inside another object (companion object or nested object)
             |  - As a standalone sealed trait/object pair
             |
             |Example:
             |  sealed trait MyEnum extends EnumEntry
             |  object MyEnum extends Enum[MyEnum] {
             |    val values = findValues
             |    case object Value1 extends MyEnum
             |    case object Value2 extends MyEnum
             |  }
             |""".stripMargin
        )
      }
    }

    val repr       = validateType[A]
    val subclasses = enclosedSubClasses[A](q)(repr, definingTpeSym)

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
    *
    * @tparam T
    *   the `Enum` type
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
    * @tparam T
    *   the `Enum` type
    * @param tpr
    *   the representation of type `T` (also specified by `tpe`)
    * @param definingModule
    *   the symbol of the module (object) where findValues is called
    */
  private[enumeratum] def enclosedSubClasses[T](q: Quotes)(
      tpr: q.reflect.TypeRepr,
      definingModule: q.reflect.Symbol
  )(using tpe: Type[T]): List[q.reflect.TypeRepr] = {
    import q.reflect.*

    given quotes: q.type = q

    @annotation.tailrec
    def isObject(sym: Symbol, ok: Boolean = false): Boolean = {
      if (!sym.flags.is(Flags.Module)) {
        false
      } else {
        val owner = sym.maybeOwner

        if (!owner.exists || owner == defn.RootClass || owner.isTerm) {
          if (
            sym.flags.is(Flags.Module) && !sym.flags.is(Flags.Package) &&
            !(sym.fullName == definingModule.fullName || sym.fullName.startsWith(
              definingModule.fullName + "."
            ))
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

    /** Check if a type symbol is a subclass of the target type symbol, ignoring type parameters.
      *
      * This allows intermediate traits with type parameters (e.g., Bar[T] extends Foo[T]) to be
      * recognized as subclasses even when we're looking for a specific parameterized type (e.g.,
      * Foo[Unit]). The check is based on the type symbol hierarchy, not exact type conformance.
      *
      * @param childTypeSym
      *   the type symbol to check
      * @return
      *   true if childTypeSym is the target or has the target in its base class hierarchy
      */
    def isSubclassOfTarget(childTypeSym: Symbol): Boolean = {
      childTypeSym == tpr.typeSymbol || childTypeSym.typeRef.baseClasses.contains(tpr.typeSymbol)
    }

    @annotation.tailrec
    def subclasses(
        children: List[Tree],
        out: List[TypeRepr]
    ): List[TypeRepr] = {
      val childTpr: Option[TypeRepr] = children.headOption.collect {
        case tpd: Typed =>
          tpd.tpt.tpe

        case vd: ValDef =>
          vd.tpt.tpe

        case cd: ClassDef =>
          cd.symbol.typeRef match {
            case TypeRef(prefix, _) =>
              prefix.select(cd.symbol)

          }
      }

      childTpr match {
        case Some(child) => {
          val tpeSym = child.typeSymbol

          // First check: is this type symbol related to our target type (ignoring type parameters)?
          // This allows intermediate traits like Bar[T] extends Foo[T] to be recognized
          // even when we're looking for Foo[Unit]
          if (isSubclassOfTarget(tpeSym)) {
            if (!isObject(tpeSym)) {
              // This is an intermediate type (trait or abstract class), not a case object
              // However, if it's a Module (object), it means isObject returned false because
              // the object is in the wrong place (already warned about). Don't double-warn.
              if (!tpeSym.flags.is(Flags.Module) && !tpeSym.flags.is(Flags.Sealed)) {
                // Only warn about unsealed intermediate types, not misplaced objects
                report.warning(
                  s"""Intermediate enum type '${tpeSym.fullName}' must be sealed.
                     |
                     |All intermediate parent types between the enum base type and the case objects must be sealed.
                     |This is a known limitation in Scala 3's macro system.
                     |
                     |To fix this, add the 'sealed' modifier to '${tpeSym.name}':
                     |  sealed trait ${tpeSym.name} extends ...
                     |  sealed abstract class ${tpeSym.name} extends ...
                     |
                     |See: https://github.com/lloydmeta/enumeratum/blob/master/README.md
                     |""".stripMargin
                )
              }
              subclasses(tpeSym.children.map(_.tree) ::: children.tail, out)
            } else {
              // This is a case object - also check that it matches the exact target type with parameters
              child.asType match {
                case ct @ '[IsEntry[t]] =>
                  subclasses(children.tail, child :: out)
                case _ =>
                  // Type symbol matches but exact type doesn't - skip it
                  subclasses(children.tail, out)
              }
            }
          } else {
            // Not related to our target type at all - skip it
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
