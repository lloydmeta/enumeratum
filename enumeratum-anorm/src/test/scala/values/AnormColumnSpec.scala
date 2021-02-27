package enumeratum.values

import scala.util.control.NonFatal

import anorm.{AnormException, SQL, SqlParser, TypeDoesNotMatch}, SqlParser.scalar

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import acolyte.jdbc.AcolyteDSL.withQueryResult
import acolyte.jdbc.Implicits._
import acolyte.jdbc.{RowList, RowLists}

final class AnormColumnSpec extends AnyWordSpec with Matchers {
  "ValueEnum" should {
    "successfully parsed as Column" when {
      def spec[T <: Drink](repr: Short, expected: T) =
        repr.toString in withQueryResult(RowLists.shortList :+ repr) { implicit con =>
          SQL("SELECT v").as(scalar[Drink].single) mustEqual expected
        }

      spec(1, Drink.OrangeJuice)
      spec(2, Drink.AppleJuice)
      spec(3, Drink.Cola)
      spec(4, Drink.Beer)
    }

    "not be parsed as Column from invalid Short representation" in {
      withQueryResult(RowLists.shortList :+ 0.toShort) { implicit con =>
        try {
          SQL("SELECT v").as(scalar[Drink].single)
          fail("Must not be successful")
        } catch {
          case NonFatal(cause) =>
            cause mustEqual AnormException(TypeDoesNotMatch(s"Invalid value: 0").message)
        }
      }
    }

    "not be parsed as Column from non-Short values" when {
      def spec(tpe: String, rowList: RowList[_]) =
        tpe in withQueryResult(rowList) { implicit con =>
          assertThrows[AnormException] {
            SQL("SELECT v").as(scalar[Drink].single)
          }
        }

      spec("float", RowLists.floatList :+ 0.12F)
      spec("String", RowLists.stringList :+ "foo")
    }
  }
}
