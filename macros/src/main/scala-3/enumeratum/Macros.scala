package test

object Macros {
  import scala.quoted.{Expr, Quotes, Type}

  inline def show[A]: String = ${ showImpl[A] }

  private def showImpl[A](using tpe: Type[A], q: Quotes): Expr[String] = {
    import q.reflect.*

    val repr = TypeRepr.of[A](using tpe)

    val tpeSym = repr.typeSymbol
    /*
     > _root_.test.Macros.show[_root_.test.Bar.type]

     val res0: String = @scala.annotation.internal.SourceFile("macros/src/main/scala-3/enumeratum/Foo.scala") object Bar extends test.Foo { this: test.Bar.type =>

     }
     */

    // val tpeSym = repr.typeSymbol.companionModule
    /*
     > _root_.test.Macros.show[_root_.test.Bar.type]

     val res0: String = lazy val Bar: test.Bar.type
     */

    Expr(tpeSym.tree.show)
  }
}
