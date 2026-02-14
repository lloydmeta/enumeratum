package enumeratum

import ContextUtils.Context

import scala.collection.immutable._
import scala.util.control.NonFatal

@SuppressWarnings(Array("org.wartremover.warts.StringPlusAny"))
object EnumMacros {

  /** Finds any [A] in the current scope and returns an expression for a list of them
    */
  def findValuesImpl[A: c.WeakTypeTag](c: Context): c.Expr[IndexedSeq[A]] = {
    import c.universe._
    val typeSymbol = weakTypeOf[A].typeSymbol
    validateType(c)(typeSymbol)
    val subclassSymbols = enclosedSubClasses(c)(typeSymbol)
    buildSeqExpr[A](c)(subclassSymbols)
  }

  /** Given an A, provides its companion
    */
  def materializeEnumImpl[A: c.WeakTypeTag](c: Context): c.Expr[A] = {
    import c.universe._
    val symbol          = weakTypeOf[A].typeSymbol
    val companionSymbol = ContextUtils.companion(c)(symbol)

    if (companionSymbol == NoSymbol) {
      c.abort(
        c.enclosingPosition,
        s"""
           |
           |  Could not find the companion object for type $symbol.
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
      c.Expr[A](Ident(companionSymbol))
    }
  }

  /** Makes sure that we can work with the given type as an enum:
    *
    * Aborts if the type is not sealed
    */
  private[enumeratum] def validateType(c: Context)(typeSymbol: c.universe.Symbol): Unit = {
    if (!typeSymbol.asClass.isSealed)
      c.abort(
        c.enclosingPosition,
        "You can only use findValues on sealed traits or classes"
      )
  }

  private[enumeratum] def enclosedSubClassTreesInModule(c: Context)(
      typeSymbol: c.universe.Symbol,
      enclosingModule: c.universe.ModuleDef
  ): List[c.universe.ModuleDef] = {
    import c.universe._
    enclosingModule.impl.body.flatMap(_ match {
      case m: ModuleDef
          if m.symbol.isModule &&
            m.symbol.asModule.moduleClass.asClass.baseClasses.contains(typeSymbol) =>
        m :: enclosedSubClassTreesInModule(c)(typeSymbol, m)

      case m: ModuleDef =>
        enclosedSubClassTreesInModule(c)(typeSymbol, m)

      case _ => List.empty
    })
  }

  /** Finds the actual trees in the current scope that implement objects of the given type
    *
    * aborts compilation if:
    *
    *   - the implementations are not all objects
    *   - the current scope is not an object
    */
  private[enumeratum] def enclosedSubClassTrees(c: Context)(
      typeSymbol: c.universe.Symbol
  ): Seq[c.universe.ModuleDef] = {
    import c.universe._
    val enclosingBodySubClassTrees: List[Tree] =
      try {
        val enclosingModule = c.enclosingClass match {
          case md @ ModuleDef(_, _, _) =>
            // Check if the enum object is nested inside a class
            val owner = md.symbol.owner
            if (owner != null && owner.isClass && !owner.isModuleClass) {
              c.warning(
                c.enclosingPosition,
                s"""Enum '${md.symbol.name}' is nested inside a class '${owner.fullName}'.
                   |
                   |This pattern is problematic because:
                   |1. Each instance of the class has its own copy of the enum, which is likely not what you want
                   |2. In Scala 3, findValues may not discover members correctly (it may return an empty collection)
                   |3. The question of identity becomes unclear: should `(new ${owner.name}).${md.symbol.name}.Member == (new ${owner.name}).${md.symbol.name}.Member`?
                   |
                   |Consider moving your enum to:
                   |  - A top-level object
                   |  - Inside another object (companion object or nested object)
                   |  - As a standalone sealed trait/object pair
                   |""".stripMargin
              )
            }
            md
          case cd: ClassDef =>
            c.abort(
              c.enclosingPosition,
              s"""The enum (i.e. the class containing the case objects and the call to `findValues`) must be an object, but was a class: ${cd.symbol.fullName}
                 |
                 |Enums must be defined as objects, not classes.
                 |
                 |Consider defining your enum as an object:
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
          case _ =>
            c.abort(
              c.enclosingPosition,
              "The enum (i.e. the class containing the case objects and the call to `findValues`) must be an object"
            )
        }
        try {
          enclosedSubClassTreesInModule(c)(typeSymbol, enclosingModule)
        } catch {
          case NonFatal(e) =>
            c.warning(
              c.enclosingPosition,
              s"Got an exception, indicating a possible bug in Enumeratum. Message: ${e.getMessage}"
            )
            List.empty
        }
      } catch {
        case NonFatal(e) =>
          c.abort(c.enclosingPosition, s"Unexpected error: ${e.getMessage}")
      }
    if (isDocCompiler(c))
      enclosingBodySubClassTrees.flatMap {
        /*
         DocDef isn't available without pulling in scala-compiler as a dependency.

         That said, DocDef *should* be the only thing that passes the prior filter
         */
        case docDef if isDocDef(c)(docDef) => {
          docDef.children.collect { case m: ModuleDef =>
            m
          }
        }
        case moduleDef: ModuleDef => List(moduleDef)
      }
    else
      enclosingBodySubClassTrees.collect { case m: ModuleDef =>
        m
      }
  }

  /** Returns a sequence of symbols for objects that implement the given type
    */
  private[enumeratum] def enclosedSubClasses(c: Context)(
      typeSymbol: c.universe.Symbol
  ): Seq[c.universe.Symbol] = {
    enclosedSubClassTrees(c)(typeSymbol).map(_.symbol)
  }

  /** Builds and returns an expression for an IndexedSeq containing the given symbols
    */
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  private[enumeratum] def buildSeqExpr[A: c.WeakTypeTag](c: Context)(
      subclassSymbols: Seq[c.universe.Symbol]
  ) = {
    import c.universe._
    val resultType = weakTypeOf[A]
    if (subclassSymbols.isEmpty) {
      c.Expr[IndexedSeq[A]](reify(IndexedSeq.empty[A]).tree)
    } else {
      c.Expr[IndexedSeq[A]](
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

  /** Returns whether or not we are in doc mode.
    *
    * It's a bit of a hack, but I don't think it's much worse than pulling in scala-compiler for the
    * sake of getting access to this class and doing an `isInstanceOf`
    */
  private[this] def isDocCompiler(c: Context): Boolean = {
    c.universe.getClass.toString.contains("doc.DocFactory")
  }

  /** Returns whether or not a given tree is a DocDef
    *
    * DocDefs are not part of the public API, so we try to hack around it here.
    */
  private[this] def isDocDef(c: Context)(t: c.universe.Tree): Boolean = {
    t.getClass.toString.contains("DocDef")
  }
}
