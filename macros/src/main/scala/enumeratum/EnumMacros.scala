package enumeratum

import scala.reflect.macros.Context

object EnumMacros {

  @deprecated("Up for deletion. Mostly done as an exercise.", "The beginning")
  def withNameImpl[A: c.WeakTypeTag](c: Context)(name: c.Expr[String]): c.Expr[A] = {
    import c.universe._
    val typeSymbol = weakTypeOf[A].typeSymbol
    val resultType = implicitly[c.WeakTypeTag[A]].tpe
    val resultTypeString = c.Expr[String](q"${resultType.toString}")
    validateType(c)(typeSymbol)
    val subclassSymbols = findSubclassSymbols(c)(typeSymbol)
    // 2.10.x has major problems unquoting a Map[String, Ident]
    val subclassNameToObjPairs: Seq[c.universe.Tree] = subclassSymbols.map { s =>
      val obj = Ident(s)
      Apply(
        Select(
          reify(Tuple2).tree,
          newTermName("apply")
        ),
        List(q"${obj.name.toString}", obj)
      )
    }
    val subclassNamesMap = Apply(
      Select(
        reify(Map).tree,
        newTermName("apply")
      ),
      subclassNameToObjPairs.toList)
    val throwBody = Throw(New(c.typeOf[IllegalArgumentException], q"""$name + " is not a child of " +  $resultTypeString"""))
    val tree =
      Apply(
        Select(subclassNamesMap, newTermName("getOrElse")),
        List(name.tree, throwBody)
      )
    c.Expr[A](tree)
  }

  def findValuesImpl[A: c.WeakTypeTag](c: Context): c.Expr[Set[A]] = {
    import c.universe._
    val resultType = implicitly[c.WeakTypeTag[A]].tpe
    val typeSymbol = weakTypeOf[A].typeSymbol
    validateType(c)(typeSymbol)
    val subclassSymbols = findSubclassSymbols(c)(typeSymbol)
    c.Expr[Set[A]](q"Set[${tq"$resultType"}](..${subclassSymbols.map(s => Ident(s))})")
  }

  private def validateType(c: Context)(typeSymbol: c.universe.Symbol): Unit = {
    if (!typeSymbol.asClass.isTrait || !typeSymbol.asClass.isSealed)
      c.abort(
        c.enclosingPosition,
        "You can only use findValues on sealed traits"
      )
  }

  private def findSubclassSymbols(c: Context)(typeSymbol: c.universe.Symbol): Seq[c.universe.Symbol] = {
    import c.universe._
    val directKnownSubclasses = typeSymbol.asClass.knownDirectSubclasses.toList // is generaly empty dunno why
    val enclosingBodySubclasses: List[Symbol] = try {
      /*
        Whem moving beyond 2.11, we should use this instead, because enclosingClass will be deprecated.

        val enclosingModuleMembers = c.internal.enclosingOwner.owner.typeSignature.decls.toList
        enclosingModule.filter { x =>
          try (x.asModule.moduleClass.asClass.baseClasses.contains(typeSymbol)) catch { case _: Throwable => false }
        }

        Unfortunately, 2.10.x does not support .enclosingOwner :P
      */
      val enclosingModule = c.enclosingClass.asInstanceOf[ModuleDef]
      enclosingModule.impl.body.filter { x =>
        try (x.symbol.asModule.moduleClass.asClass.baseClasses.contains(typeSymbol)) catch { case _: Throwable => false }
      }.map(_.symbol)
    } catch { case _: Throwable => Nil }
    val subclasses = directKnownSubclasses ::: enclosingBodySubclasses
    if (!subclasses.forall(x => x.isModule))
      c.abort(c.enclosingPosition, "All subclasses must be objects.")
    else subclasses
  }

}