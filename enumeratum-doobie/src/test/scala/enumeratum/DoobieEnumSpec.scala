package enumeratum

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import doobie.util.{Read => DoobieRead, Write => DoobieWrite}
import scala.collection.immutable

class DoobieEnumSpec extends AnyFunSpec with Matchers {

  import doobie.generic.auto._

  describe("A DoobieEnum") {

    it("should have a Write") {
      DoobieWrite[DoobieShirt]
    }

    it("should have a Read") {
      DoobieRead[DoobieShirt]
    }

  }

}

sealed trait DoobieShirtSize extends EnumEntry

case object DoobieShirtSize extends Enum[DoobieShirtSize] with DoobieEnum[DoobieShirtSize] {

  case object Small  extends DoobieShirtSize
  case object Medium extends DoobieShirtSize
  case object Large  extends DoobieShirtSize

  override val values: immutable.IndexedSeq[DoobieShirtSize] = findValues

}

case class DoobieShirt(size: DoobieShirtSize)
