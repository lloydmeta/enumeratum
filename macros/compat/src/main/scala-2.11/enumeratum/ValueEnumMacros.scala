package enumeratum

import scala.reflect.ClassTag
import ContextUtils.Context

object ValueEnumMacros {

  def findIntValueEntriesImpl[ValueEntryType: c.WeakTypeTag](c: Context): c.Expr[IndexedSeq[ValueEntryType]] = {
    findValueEntriesImpl[ValueEntryType, Int, Int](c)(identity)
  }

  def findLongValueEntriesImpl[ValueEntryType: c.WeakTypeTag](c: Context): c.Expr[IndexedSeq[ValueEntryType]] = {
    findValueEntriesImpl[ValueEntryType, Long, Long](c)(identity)
  }

  def findShortValueEntriesImpl[ValueEntryType: c.WeakTypeTag](c: Context): c.Expr[IndexedSeq[ValueEntryType]] = {
    findValueEntriesImpl[ValueEntryType, Int, Short](c)(_.toShort) // do a transform because there is no such thing as Short literals
  }

  private[this] def findValueEntriesImpl[ValueEntryType: c.WeakTypeTag, ValueTypeType <: AnyVal: ClassTag, ProcessedValue](c: Context)(processFoundValues: ValueTypeType => ProcessedValue): c.Expr[IndexedSeq[ValueEntryType]] = {
    import c.universe._
    val resultType = implicitly[c.WeakTypeTag[ValueEntryType]].tpe
    val typeSymbol = weakTypeOf[ValueEntryType].typeSymbol
    EnumMacros.validateType(c)(typeSymbol)
    val subclassTrees = EnumMacros.enclosedSubClassTrees(c)(typeSymbol)
    val treeWithVals = findValuesForSubclassTrees[ValueTypeType, ProcessedValue](c)(subclassTrees, processFoundValues)
    ensureUnique[ProcessedValue](c)(treeWithVals)
    val subclassSymbols = treeWithVals.map(_.tree.symbol)
    if (subclassSymbols.isEmpty) {
      c.Expr[IndexedSeq[ValueEntryType]](reify(IndexedSeq.empty[ValueEntryType]).tree)
    } else {
      c.Expr[IndexedSeq[ValueEntryType]](
        Apply(
          TypeApply(
            Select(reify(IndexedSeq).tree, ContextUtils.termName(c)("apply")),
            List(TypeTree(resultType))
          ),
          subclassSymbols.map(Ident(_)).toList
        )
      )
    }
  }

  private[this] def findValuesForSubclassTrees[ValueType: ClassTag, ProcessedValueType](c: Context)(memberTrees: Seq[c.universe.Tree], processFoundValues: ValueType => ProcessedValueType): Seq[TreeWithVal[c.universe.Tree, ProcessedValueType]] = {
    val treeWithValues = toTreeWithValue[ValueType, ProcessedValueType](c)(memberTrees, processFoundValues)
    val (hasValueMember, lacksValueMember) = treeWithValues.partition(_.maybeValue.isDefined)
    if (lacksValueMember.nonEmpty) {
      val classTag = implicitly[ClassTag[ValueType]]
      val lacksValueMemberStr = lacksValueMember.map(_.tree.symbol).mkString(", ")
      c.abort(
        c.enclosingPosition,
        s"It looks like not all of the members have a literal/constant 'value:${classTag.runtimeClass}' declaration, namely: $lacksValueMemberStr."
      )
    }
    hasValueMember.collect {
      case TreeWithMaybeVal(tree, Some(v)) => TreeWithVal(tree, v)
    }
  }

  private[this] def toTreeWithValue[ValueType: ClassTag, ProcessedValueType](c: Context)(memberTrees: Seq[c.universe.Tree], processFoundValues: ValueType => ProcessedValueType): Seq[TreeWithMaybeVal[c.universe.Tree, ProcessedValueType]] = {
    import c.universe._
    val classTag = implicitly[ClassTag[ValueType]]
    val valueTerm = ContextUtils.termName(c)("value") /* ContextUtils.termName(c)("value") when > 2.10 */
    memberTrees.map { declTree =>
      // TODO see if we can collectFirst
      val values = declTree.collect {
        case ValDef(_, termName, _, Literal(Constant(i: ValueType))) if termName == valueTerm => Some(i)
        case Apply(fun, args) if fun.tpe != null => {
          val funTpe = fun.tpe
          val members: c.universe.MemberScope = funTpe.members
          val valueTerms = members.collect {
            case constr if constr.isConstructor => {
              val asMethod = constr.asMethod
              val paramTermNames = asMethod.paramLists.flatten.map(_.asTerm.name)
              val paramsWithArg = paramTermNames.zip(args)
              paramsWithArg.collectFirst {
                case (`valueTerm`, Literal(Constant(i: ValueType))) => i
                case (`valueTerm`, Literal(Constant(i))) => c.abort(c.enclosingPosition, s"${declTree.symbol} has a value with the wrong type: $i:${i.getClass}, instead of ${classTag.runtimeClass}.")
                // Can't match without using Ident(ContextUtils.termName(c)(" ")) extractor ??!
                case (_, AssignOrNamedArg(Ident(TermName("value")), Literal(Constant(i: ValueType)))) => i
                case (_, AssignOrNamedArg(Ident(TermName("value")), Literal(Constant(i)))) => c.abort(c.enclosingPosition, s"${declTree.symbol} has a value with the wrong type: $i:${i.getClass}, instead of ${classTag.runtimeClass}")
                case (`valueTerm`, _) => c.abort(c.enclosingPosition, s"${declTree.symbol} has a non-literal 'value'")
                case (_, AssignOrNamedArg(Ident(TermName("value")), _)) => c.abort(c.enclosingPosition, s"${declTree.symbol} has a non-literal 'value'")
              }
            }
          }
          valueTerms.find(_.isDefined).flatten
        }
      }
      TreeWithMaybeVal(declTree, values.headOption.flatten.map(processFoundValues))
    }
  }

  private[this] def ensureUnique[A](c: Context)(treeWithVals: Seq[TreeWithVal[c.universe.Tree, A]]): Unit = {
    val membersWithValues = treeWithVals.map { treeWithVal =>
      treeWithVal.tree.symbol -> treeWithVal.value
    }
    val groupedByValue = membersWithValues.groupBy(_._2).mapValues(_.map(_._1))
    val (valuesWithOneSymbol, valuesWithMoreThanOneSymbol) = groupedByValue.partition(_._2.size <= 1)
    if (valuesWithOneSymbol.size != membersWithValues.distinct.size) {
      c.abort(
        c.enclosingPosition,
        s"It does not look like you have unique values. Found the following values correspond to more than one members: $valuesWithMoreThanOneSymbol"
      )
    }
  }

  private[this] case class TreeWithMaybeVal[CTree, T](tree: CTree, maybeValue: Option[T])
  private[this] case class TreeWithVal[CTree, T](tree: CTree, value: T)

}