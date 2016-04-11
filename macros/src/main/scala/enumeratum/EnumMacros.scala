package enumeratum

import scala.reflect.macros.Context
import scala.util.control.NonFatal

// TODO switch to blackbox.Context when dropping support for 2.10.x

object EnumMacros {

  def findIntValuesImp[A: c.WeakTypeTag](c: Context): c.Expr[IndexedSeq[A]] = {
    import c.universe._
    val resultType = implicitly[c.WeakTypeTag[A]].tpe
    val typeSymbol = weakTypeOf[A].typeSymbol

    validateType(c)(typeSymbol)
    val subclassTrees = enclosedSubClassTrees(c)(typeSymbol)
    val treeWithVals = ensureHasValuesDeclarations[Int](c)(subclassTrees)
    ensureUnique[Int](c)(treeWithVals)
    val subclassSymbols = treeWithVals.map(_.tree.symbol)
    if (subclassSymbols.isEmpty) {
      c.Expr[IndexedSeq[A]](reify(IndexedSeq.empty[A]).tree)
    } else {
      c.Expr[IndexedSeq[A]](
        Apply(
          TypeApply(
            Select(reify(IndexedSeq).tree, newTermName("apply")),
            List(TypeTree(resultType))
          ),
          subclassSymbols.map(Ident(_)).toList
        )
      )
    }
  }

  def findValuesImpl[A: c.WeakTypeTag](c: Context): c.Expr[IndexedSeq[A]] = {
    import c.universe._
    val resultType = implicitly[c.WeakTypeTag[A]].tpe
    val typeSymbol = weakTypeOf[A].typeSymbol
    validateType(c)(typeSymbol)
    val subclassSymbols = enclosedSubClasses(c)(typeSymbol)
    if (subclassSymbols.isEmpty) {
      c.Expr[IndexedSeq[A]](reify(IndexedSeq.empty[A]).tree)
    } else {
      c.Expr[IndexedSeq[A]](
        Apply(
          TypeApply(
            Select(reify(IndexedSeq).tree, newTermName("apply")),
            List(TypeTree(resultType))
          ),
          subclassSymbols.map(Ident(_)).toList
        )
      )
    }
  }


  private [this]  def validateType(c: Context)(typeSymbol: c.universe.Symbol): Unit = {
      if (!typeSymbol.asClass.isSealed)
        c.abort(
          c.enclosingPosition,
          "You can only use findValues on sealed traits or classes"
        )
    }

  private [this]  def ensureHasValuesDeclarations[A](c: Context)(memberTrees: Seq[c.universe.Tree]): Seq[TreeWithVal[c.universe.Tree, A]] = {
      import c.universe._
      val treeWithValues = toTreeWithValue[A](c)(memberTrees)
      val (hasValueMember , lacksValueMember) = treeWithValues.partition(_.maybeValue.isDefined)
      if(lacksValueMember.nonEmpty) {
        c.abort(
          c.enclosingPosition,
          s"It looks like not all of the members have a 'value' declaration, namely: $lacksValueMember"
        )
      }
      hasValueMember.collect {
        case TreeWithMaybeVal(tree, Some(v)) => TreeWithVal(tree, v)
      }
    }

  private [this]  def toTreeWithValue[A](c: Context)(memberTrees: Seq[c.universe.Tree]): Seq[TreeWithMaybeVal[c.universe.Tree, A]] = {
      import c.universe._
      memberTrees.map { declTree =>
        // TODO see if we can collectFirst
        val values = declTree.collect {
          case ValDef(_, TermName("value"), _, Literal(Constant(i: A))) => Some(i)
          case Apply(fun, args) if fun.tpe != null => {
            val funTpe = fun.tpe
            val members = funTpe.members
            val valueTerm = members.collect {
              case constr if constr.isConstructor => {
                val asMethod = constr.asMethod
                val paramList = asMethod.paramLists.flatten.map(_.asTerm)
                val paramsWithArg = paramList.zip(args)
                paramsWithArg.find(_._1.name == TermName("value")).collectFirst {
                  case (_, Literal(Constant(i: A))) => i
                }
              }
            }
            valueTerm.flatten.headOption
          }
        }
        TreeWithMaybeVal(declTree, values.headOption.flatten)
      }
    }

  private [this]  def ensureUnique[A](c: Context)(treeWithVals: Seq[TreeWithVal[c.universe.Tree, A]]): Unit = {
      val membersWithValues = treeWithVals.map { treeWithVal =>
        treeWithVal.tree.symbol -> treeWithVal.value
      }
      val groupedByValue = membersWithValues.groupBy(_._2).mapValues(_.map(_._1))
      val (valuesWithOneSymbol, valuesWithMoreThanOneSymbol) = groupedByValue.partition(_._2.size <= 1)
      if(valuesWithOneSymbol.size != membersWithValues.distinct.size) {
        c.abort(
          c.enclosingPosition,
          s"It does not look like you have unique values. Found the following values correspond to more than one members: $valuesWithMoreThanOneSymbol"
        )
      }
    }

  private [this]  def enclosedSubClassTrees(c: Context)(typeSymbol: c.universe.Symbol): Seq[c.universe.Tree] = {
      import c.universe._
      val enclosingBodySubClassTrees: List[Tree] = try {
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
        }
      } catch { case NonFatal(e) => c.abort(c.enclosingPosition, s"Unexpected error: ${e.getMessage}") }
      if (!enclosingBodySubClassTrees.forall(x => x.symbol.isModule))
        c.abort(c.enclosingPosition, "All subclasses must be objects.")
      else enclosingBodySubClassTrees
    }

  private [this] def enclosedSubClasses(c: Context)(typeSymbol: c.universe.Symbol): Seq[c.universe.Symbol] = {
      enclosedSubClassTrees(c)(typeSymbol).map(_.symbol)
    }


  private [this]  case class TreeWithMaybeVal[CTree, T](tree: CTree, maybeValue: Option[T])
  private [this]  case class TreeWithVal[CTree, T](tree: CTree, value: T)


}
