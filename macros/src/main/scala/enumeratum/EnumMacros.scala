package enumeratum

import scala.reflect.macros.Context
import scala.util.control.NonFatal

// TODO switch to blackbox.Context when dropping support for 2.10.x

object EnumMacros {

  def findValuesImpl[A: c.WeakTypeTag](c: Context): c.Expr[IndexedSeq[A]] = {
    import c.universe._
    val resultType = implicitly[c.WeakTypeTag[A]].tpe
    val typeSymbol = weakTypeOf[A].typeSymbol
    validateType(c)(typeSymbol)
    val subclassSymbols = enclosedSubClasses(c)(typeSymbol)
    if (subclassSymbols.isEmpty) {
      c.Expr[IndexedSeq[A]](reify(IndexedSeq.empty[A]).tree)
    } else {
      val seqTypeTree = AppliedTypeTree(
        Select(Select(Ident(newTermName("scala")), newTermName("collection")), newTypeName("IndexedSeq")),
        List(TypeTree(resultType))
      )
      val typeCheckType = appliedType(
        weakTypeOf[IndexedSeq[_]].typeConstructor,
        List(resultType)
      )
      val typed = c.typeCheck(
        tree = Typed(
          Apply(
            Select(reify(IndexedSeq).tree, newTermName("apply")),
            subclassSymbols.map(Ident(_)).toList
          ),
          seqTypeTree
        ),
        pt = typeCheckType
      )
      c.Expr[IndexedSeq[A]](typed)
    }
  }

  private[this] def validateType(c: Context)(typeSymbol: c.universe.Symbol): Unit = {
    if (!typeSymbol.asClass.isSealed)
      c.abort(
        c.enclosingPosition,
        "You can only use findValues on sealed traits or classes"
      )
  }

  private[this] def enclosedSubClasses(c: Context)(typeSymbol: c.universe.Symbol): Seq[c.universe.Symbol] = {
    import c.universe._
    val enclosingBodySubclasses: List[Symbol] = try {
      /*
        When moving beyond 2.11, we should use this instead, because enclosingClass will be deprecated.

        val enclosingModuleMembers = c.internal.enclosingOwner.owner.typeSignature.decls.toList
        enclosingModuleMembers.filter { x =>
          try (x.asModule.moduleClass.asClass.baseClasses.contains(typeSymbol)) catch { case _: Throwable => false }
        }

        Unfortunately, 2.10.x does not support .enclosingOwner :P
      */
      val enclosingModule = c.enclosingClass match {
        case md @ ModuleDef(_, _, _) => md
        case _ => c.abort(
          c.enclosingPosition,
          "The enum (i.e. the class containing the case objects and the call to `findValues`) must be an object"
        )
      }
      enclosingModule.impl.body.filter { x =>
        try {
          Option(x.symbol) match {
            case Some(sym) if sym.isModule => sym.asModule.moduleClass.asClass.baseClasses.contains(typeSymbol)
            case _ => false
          }
        } catch {
          case NonFatal(e) =>
            c.warning(c.enclosingPosition, s"Got an exception, indicating a possible bug in Enumeratum. Message: ${e.getMessage}")
            false
        }
      }.map(_.symbol)
    } catch { case NonFatal(e) => c.abort(c.enclosingPosition, s"Unexpected error: ${e.getMessage}") }
    if (!enclosingBodySubclasses.forall(x => x.isModule))
      c.abort(c.enclosingPosition, "All subclasses must be objects.")
    else enclosingBodySubclasses
  }

}
