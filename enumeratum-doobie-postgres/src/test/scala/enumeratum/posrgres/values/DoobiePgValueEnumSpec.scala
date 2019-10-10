package enumeratum.posrgres.values

import org.scalatest.{FunSpec, Matchers}
import doobie.{Meta, Read, Write}
import enumeratum.postgres.values.DoobiePgValueEnum
import enumeratum.values.{StringEnum, StringEnumEntry}

import scala.collection.immutable

class DoobiePgValueEnumSpec extends FunSpec with Matchers {
  describe("A StringDoobieEnum") {

    it("should have a Write") {
      Write[DoobieComputer]
    }

    it("should have a Read") {
      Read[DoobieComputer]
    }

  }
}

sealed abstract class DoobieOperatingSystem(val value: String) extends StringEnumEntry
case object DoobieOperatingSystem extends StringEnum[DoobieOperatingSystem] {

  case object Linux   extends DoobieOperatingSystem("linux")
  case object OSX     extends DoobieOperatingSystem("osx")
  case object Windows extends DoobieOperatingSystem("windows")
  case object Android extends DoobieOperatingSystem("android")

  override val values: immutable.IndexedSeq[DoobieOperatingSystem] = findValues
  implicit val doobieMeta: Meta[DoobieOperatingSystem] =
    DoobiePgValueEnum.meta("operating_system", DoobieOperatingSystem)
}

case class DoobieComputer(operatingSystem: DoobieOperatingSystem)
