package enumeratum

import scala.reflect.ClassTag

import scala.deriving.Mirror
import scala.quoted.{Expr, FromExpr, Quotes, Type}

import enumeratum.values.AllowAlias

/** @define valueEntryTypeNote
  *   Note, requires the ValueEntryType to have a 'value' member that has a literal value.
  */
@SuppressWarnings(Array("org.wartremover.warts.StringPlusAny"))
object ValueEnumMacros {

  /** Finds ValueEntryType-typed objects in scope that have literal `value: Int` implementations.
    *
    * $valueEntryTypeNote
    */
  def findIntValueEntriesImpl[ValueEntryType: Type](using
      Quotes
  ): Expr[IndexedSeq[ValueEntryType]] =
    findValueEntriesImpl[ValueEntryType, Int]

  /** Finds ValueEntryType-typed objects in scope that have literal `value: Long` implementations.
    *
    * $valueEntryTypeNote
    */
  def findLongValueEntriesImpl[ValueEntryType: Type](using
      Quotes
  ): Expr[IndexedSeq[ValueEntryType]] =
    findValueEntriesImpl[ValueEntryType, Long]

  /** Finds ValueEntryType-typed objects in scope that have literal `value: Short` implementations.
    *
    * Note
    *
    *   - requires the ValueEntryType to have a 'value' member that has a literal value
    *   - the Short value should be a literal Int (do no need to cast .toShort).
    */
  def findShortValueEntriesImpl[ValueEntryType: Type](using
      Quotes
  ): Expr[IndexedSeq[ValueEntryType]] =
    findValueEntriesImpl[ValueEntryType, Short]

  /** Finds ValueEntryType-typed objects in scope that have literal `value: String` implementations.
    *
    * Note
    *
    *   - requires the ValueEntryType to have a 'value' member that has a literal value
    */
  def findStringValueEntriesImpl[ValueEntryType: Type](using
      Quotes
  ): Expr[IndexedSeq[ValueEntryType]] =
    findValueEntriesImpl[ValueEntryType, String]

  /** Finds ValueEntryType-typed objects in scope that have literal `value: Byte` implementations.
    *
    * Note
    *
    *   - requires the ValueEntryType to have a 'value' member that has a literal value
    */
  def findByteValueEntriesImpl[ValueEntryType: Type](using
      Quotes
  ): Expr[IndexedSeq[ValueEntryType]] =
    findValueEntriesImpl[ValueEntryType, Byte]

  /** Finds ValueEntryType-typed objects in scope that have literal `value: Char` implementations.
    *
    * Note
    *
    *   - requires the ValueEntryType to have a 'value' member that has a literal value
    */
  def findCharValueEntriesImpl[ValueEntryType: Type](using
      Quotes
  ): Expr[IndexedSeq[ValueEntryType]] =
    findValueEntriesImpl[ValueEntryType, Char]

  private given ValueOfFromExpr[T <: Singleton](using Type[T]): FromExpr[ValueOf[T]] with {
    def unapply(x: Expr[ValueOf[T]])(using q: Quotes): Option[ValueOf[T]] = {
      import q.reflect.*

      x match {
        case '{ new ValueOf[T]($v) } =>
          v.asTerm match {
            case id: Ident => {
              val cls         = Class.forName(id.symbol.fullName + '$')
              val moduleField = cls.getFields.find(_.getName == f"MODULE$$")

              moduleField.map { field =>
                new ValueOf(field.get(null).asInstanceOf[T])
              }
            }

            case _ =>
              None
          }

        case _ =>
          None
      }
    }
  }

  /** The method that does the heavy lifting.
    */
  private def findValueEntriesImpl[A, ValueType](using
      q: Quotes,
      tpe: Type[A],
      valueTpe: Type[ValueType]
  )(using cls: ClassTag[ValueType]): Expr[IndexedSeq[A]] = {
    type SingletonHead[Head <: A & Singleton, Tail <: Tuple] = Head *: Tail
    type OtherHead[Head <: A, Tail <: Tuple]                 = Head *: Tail

    type SumOf[X <: A, T <: Tuple] = Mirror.SumOf[X] {
      type MirroredElemTypes = T
    }

    import q.reflect.*

    val ctx = q match {
      case quotesImpl: scala.quoted.runtime.impl.QuotesImpl => quotesImpl.ctx
      case other => report.errorAndAbort(s"[${other}] was not the expected class")
    }

    val yRetainTrees = ctx.settings.YretainTrees.valueIn(ctx.settingsState)

    if (!yRetainTrees) {
      report.errorAndAbort("""Option -Yretain-trees must be set in scalacOptions.
In SBT settings:

  scalacOptions += "-Yretain-trees"
""")
    }

    val repr   = TypeRepr.of[A]
    val tpeSym = repr.typeSymbol

    val valueRepr = TypeRepr.of[ValueType]

    val valueParamIndex = tpeSym.primaryConstructor.paramSymss
      .filterNot(_.exists(_.isType))
      .flatten
      .zipWithIndex
      .collectFirst {
        case (p, i) if p.name == "value" => i
      }

    type IsValue[T <: ValueType] = T

    object ConstVal {
      @annotation.tailrec
      def unapply(tree: Tree): Option[Constant] = tree match {
        case NamedArg("value", v)                            => unapply(v)
        case ValDef("value", _, Some(v))                     => unapply(v)
        case lit @ Literal(const) if (lit.tpe <:< valueRepr) => Some(const)
        case _                                               => None
      }
    }

    @annotation.tailrec
    def collect[T <: Tuple](
        instances: List[Expr[A]],
        values: Map[TypeRepr, ValueType]
    )(using tupleTpe: Type[T]): Either[String, Expr[List[A]]] =
      tupleTpe match {
        case '[SingletonHead[h, tail]] => {
          val htpr = TypeRepr.of[h]

          (for {
            vof <- Expr.summon[ValueOf[h]]
            constValue <- htpr.typeSymbol.tree match {
              case ClassDef(_, _, parents, _, statements) => {
                val fromCtor = valueParamIndex.flatMap { (ix: Int) =>
                  parents
                    .collectFirst {
                      case Apply(Select(New(id), _), args) if id.tpe <:< repr               => args
                      case Apply(TypeApply(Select(New(id), _), _), args) if id.tpe <:< repr => args
                    }
                    .flatMap(_.lift(ix).collect { case ConstVal(const) => const })
                }
                def fromBody = statements.collectFirst { case ConstVal(v) => v }
                fromCtor.orElse(fromBody).flatMap { const => cls.unapply(const.value) }
              }
              case _ =>
                Option.empty[ValueType]
            }
          } yield Tuple3(TypeRepr.of[h], '{ ${ vof }.value: A }, constValue)) match {
            case Some((tpr, instance, value)) =>
              collect[tail](instance :: instances, values + (tpr -> value))

            case None =>
              report.errorAndAbort(
                s"Fails to check value entry ${htpr.show} for enum ${repr.show}"
              )
          }
        }

        case '[OtherHead[h, tail]] =>
          Expr.summon[Mirror.SumOf[h]] match {
            case Some(sum) =>
              sum.asTerm.tpe.asType match {
                case '[SumOf[a, t]] => collect[Tuple.Concat[t, tail]](instances, values)
                case _              => Left(s"Invalid `Mirror.SumOf[${TypeRepr.of[h].show}]")
              }

            case None =>
              Left(s"Missing `Mirror.SumOf[${TypeRepr.of[h].show}]`")
          }

        case '[EmptyTuple] => {
          val allowAlias = repr <:< TypeRepr.of[AllowAlias]

          if (!allowAlias && values.values.toSet.size < values.size) {
            val details = values
              .map { case (sub, value) =>
                s"${sub.show} = $value"
              }
              .mkString(", ")

            Left(s"Values value are not discriminated subtypes: ${details}")
          } else {
            Right(Expr.ofList(instances.reverse))
          }
        }
      }

    val result: Either[String, Expr[List[A]]] =
      Expr.summon[Mirror.SumOf[A]] match {
        case Some(sum) =>
          sum.asTerm.tpe.asType match {
            case '[SumOf[A, t]] =>
              collect[t](List.empty, Map.empty)

            case _ =>
              Left(s"Invalid `Mirror.SumOf[${repr.show}]`")

          }

        case None =>
          Left(s"Missing `Mirror.SumOf[${repr.show}]`")
      }

    result match {
      case Left(errorMsg) =>
        report.errorAndAbort(errorMsg)

      case Right(instances) =>
        '{ IndexedSeq.empty ++ $instances }
    }
  }
}
