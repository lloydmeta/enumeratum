package enumeratum

import scala.util.control.NonFatal

import anorm.{AnormException, SQL, SqlParser, TypeDoesNotMatch}, SqlParser.scalar

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import acolyte.jdbc.AcolyteDSL.withQueryResult
import acolyte.jdbc.Implicits._
import acolyte.jdbc.{RowList, RowLists}

final class AnormColumnSpec extends AnyWordSpec with Matchers {
  "Sensitive enum" should {
    "successfully parsed as Column" when {
      def spec[T <: Dummy](repr: String, expected: T) =
        repr in withQueryResult(RowLists.stringList :+ repr) { implicit con =>
          SQL("SELECT v").as(scalar[Dummy].single) mustEqual expected
        }

      spec("A", Dummy.A)
      spec("B", Dummy.B)
      spec("c", Dummy.c)
    }

    "not be parsed as Column from invalid String representation" when {
      def spec(title: String, repr: String) =
        title in withQueryResult(RowLists.stringList :+ repr) { implicit con =>
          try {
            SQL("SELECT v").as(scalar[Dummy].single)
            fail(s"Must not be successful: $repr")
          } catch {
            case NonFatal(cause) =>
              cause mustEqual AnormException(TypeDoesNotMatch(s"Invalid value: $repr").message)
          }
        }

      spec("a (!= A as sensitive)", "a")
      spec("b (!= B as sensitive)", "b")
      spec("C (!= c as sensitive)", "C")
    }

    "not be parsed as Column from non-String values" when {
      def spec(tpe: String, rowList: RowList[_]) =
        tpe in withQueryResult(rowList) { implicit con =>
          try {
            SQL("SELECT v").as(scalar[Dummy].single)
            fail(s"Must not be successful: $tpe")
          } catch {
            case NonFatal(cause) =>
              cause mustEqual AnormException(
                TypeDoesNotMatch(s"Column '.null' expected to be String; Found $tpe").message)
          }
        }

      spec("float", RowLists.floatList :+ 0.1F)
      spec("int", RowLists.intList :+ 1)
    }
  }

  "Insensitive enum" should {
    "successfully parsed as Column" when {
      def spec[T <: InsensitiveDummy](repr: String, expected: T) =
        repr in withQueryResult(RowLists.stringList :+ repr) { implicit con =>
          SQL("SELECT v").as(scalar[InsensitiveDummy].single) mustEqual expected
        }

      spec("A", InsensitiveDummy.A)
      spec("a", InsensitiveDummy.A)

      spec("B", InsensitiveDummy.B)
      spec("b", InsensitiveDummy.B)

      spec("C", InsensitiveDummy.c)
      spec("c", InsensitiveDummy.c)
    }

    "not be parsed as Column from non-String values" when {
      def spec(tpe: String, rowList: RowList[_]) =
        tpe in withQueryResult(rowList) { implicit con =>
          try {
            SQL("SELECT v").as(scalar[InsensitiveDummy].single)
            fail(s"Must not be successful: $tpe")
          } catch {
            case NonFatal(cause) =>
              cause mustEqual AnormException(
                TypeDoesNotMatch(s"Column '.null' expected to be String; Found $tpe").message)
          }
        }

      spec("float", RowLists.floatList :+ 0.1F)
      spec("int", RowLists.intList :+ 1)
    }
  }

  "Lowercase enum" should {
    "successfully parsed as Column" when {
      def spec[T <: LowercaseDummy](repr: String, expected: T) =
        repr in withQueryResult(RowLists.stringList :+ repr) { implicit con =>
          SQL("SELECT v").as(scalar[LowercaseDummy].single) mustEqual expected
        }

      spec("apple", LowercaseDummy.Apple)
      spec("banana", LowercaseDummy.Banana)
      spec("cherry", LowercaseDummy.Cherry)
    }

    "not be parsed as Column from invalid String representation" when {
      def spec(title: String, repr: String) =
        title in withQueryResult(RowLists.stringList :+ repr) { implicit con =>
          try {
            SQL("SELECT v").as(scalar[LowercaseDummy].single)
            fail(s"Must not be successful: $repr")
          } catch {
            case NonFatal(cause) =>
              cause mustEqual AnormException(TypeDoesNotMatch(s"Invalid value: $repr").message)
          }
        }

      spec("Apple (!= apple as lowercase)", "Apple")
      spec("BANANA (!= banana as lowercase)", "BANANA")
      spec("Cherry (!= cherry as lowercase)", "Cherry")
    }

    "not be parsed as Column from non-String values" when {
      def spec(tpe: String, rowList: RowList[_]) =
        tpe in withQueryResult(rowList) { implicit con =>
          try {
            SQL("SELECT v").as(scalar[LowercaseDummy].single)
            fail(s"Must not be successful: $tpe")
          } catch {
            case NonFatal(cause) =>
              cause mustEqual AnormException(
                TypeDoesNotMatch(s"Column '.null' expected to be String; Found $tpe").message)
          }
        }

      spec("float", RowLists.floatList :+ 0.1F)
      spec("int", RowLists.intList :+ 1)
    }
  }

  "Uppercase enum" should {
    "successfully parsed as Column" when {
      def spec[T <: UppercaseDummy](repr: String, expected: T) =
        repr in withQueryResult(RowLists.stringList :+ repr) { implicit con =>
          SQL("SELECT v").as(scalar[UppercaseDummy].single) mustEqual expected
        }

      spec("APPLE", UppercaseDummy.Apple)
      spec("BANANA", UppercaseDummy.Banana)
      spec("CHERRY", UppercaseDummy.Cherry)
    }

    "not be parsed as Column from invalid String representation" when {
      def spec(title: String, repr: String) =
        title in withQueryResult(RowLists.stringList :+ repr) { implicit con =>
          try {
            SQL("SELECT v").as(scalar[UppercaseDummy].single)
            fail(s"Must not be successful: $repr")
          } catch {
            case NonFatal(cause) =>
              cause mustEqual AnormException(TypeDoesNotMatch(s"Invalid value: $repr").message)
          }
        }

      spec("Apple (!= APPLE as uppercase)", "Apple")
      spec("banana (!= BANANA as uppercase)", "banana")
      spec("cherry (!= CHERRY as uppercase)", "Cherry")
    }

    "not be parsed as Column from non-String values" when {
      def spec(tpe: String, rowList: RowList[_]) =
        tpe in withQueryResult(rowList) { implicit con =>
          try {
            SQL("SELECT v").as(scalar[UppercaseDummy].single)
            fail(s"Must not be successful: $tpe")
          } catch {
            case NonFatal(cause) =>
              cause mustEqual AnormException(
                TypeDoesNotMatch(s"Column '.null' expected to be String; Found $tpe").message)
          }
        }

      spec("float", RowLists.floatList :+ 0.1F)
      spec("int", RowLists.intList :+ 1)
    }
  }
}
