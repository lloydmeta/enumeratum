package enumeratum.posrgres

import enumeratum._
import org.scalatest.{FunSpec, Matchers}
import doobie.{Read, Write, Meta}
import enumeratum.postgres.DoobiePgEnum

import scala.collection.immutable

class DoobiePgEnumSpec extends FunSpec with Matchers {
  describe("A DoobieEnum") {

    it("should have a Write") {
      Write[DoobieShirt]
    }

    it("should have a Read") {
      Read[DoobieShirt]
    }

  }
}

sealed trait DoobieShirtSize extends EnumEntry
case object DoobieShirtSize extends Enum[DoobieShirtSize] {
  case object Small  extends DoobieShirtSize
  case object Medium extends DoobieShirtSize
  case object Large  extends DoobieShirtSize

  override val values: immutable.IndexedSeq[DoobieShirtSize] = findValues
  implicit val doobieMeta: Meta[DoobieShirtSize]             = DoobiePgEnum.meta("shirt_size", DoobieShirtSize)
}

case class DoobieShirt(size: DoobieShirtSize)
