package enumeratum.values

import cats.effect.{IO, Resource}
import doobie.util.{Read => DoobieRead, Write => DoobieWrite}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.collection.immutable
import java.util.concurrent.{Executors, ExecutorService}

class DoobieValueEnumSpec extends AnyFunSpec with Matchers {

  import doobie.generic.auto._

  describe("An IntDoobieEnum") {

    it("should have a Write") {
      DoobieWrite[DoobieBorrowerToLibraryItem]
    }

    it("should have a Read") {
      DoobieRead[DoobieBorrowerToLibraryItem]
    }

  }

  describe("A LongDoobieEnum") {

    it("should have a Write") {
      DoobieWrite[DoobieContent]
    }

    it("should have a Read") {
      DoobieRead[DoobieContent]
    }

  }

  describe("A ShortDoobieEnum") {

    it("should have a Write") {
      DoobieWrite[DoobieDrinkManufacturer]
    }

    it("should have a Read") {
      DoobieRead[DoobieDrinkManufacturer]
    }

  }

  describe("A StringDoobieEnum") {

    it("should have a Write") {
      DoobieWrite[DoobieComputer]
    }

    it("should have a Read") {
      DoobieRead[DoobieComputer]
    }

  }

  describe("A CharDoobieEnum") {

    it("should have a Write") {
      DoobieWrite[DoobieName]
    }

    it("should have a Read") {
      DoobieRead[DoobieName]
    }

  }

  describe("A ByteDoobieEnum") {

    it("should have a Write") {
      DoobieWrite[DoobieChar]
    }

    it("should have a Read") {
      DoobieRead[DoobieChar]
    }

  }

}

sealed abstract class DoobieLibraryItem(val value: Int, val name: String) extends IntEnumEntry

case object DoobieLibraryItem
    extends IntEnum[DoobieLibraryItem]
    with IntDoobieEnum[DoobieLibraryItem] {

  // A good mix of named, unnamed, named + unordered args
  case object Book     extends DoobieLibraryItem(value = 1, name = "book")
  case object Movie    extends DoobieLibraryItem(name = "movie", value = 2)
  case object Magazine extends DoobieLibraryItem(3, "magazine")
  case object CD       extends DoobieLibraryItem(4, name = "cd")

  override val values: immutable.IndexedSeq[DoobieLibraryItem] = findValues

}

case class DoobieBorrowerToLibraryItem(borrower: String, item: DoobieLibraryItem)

sealed abstract class DoobieContentType(val value: Long, name: String) extends LongEnumEntry

case object DoobieContentType
    extends LongEnum[DoobieContentType]
    with LongDoobieEnum[DoobieContentType] {

  override val values: immutable.IndexedSeq[DoobieContentType] = findValues

  case object Text  extends DoobieContentType(value = 1L, name = "text")
  case object Image extends DoobieContentType(value = 2L, name = "image")
  case object Video extends DoobieContentType(value = 3L, name = "video")
  case object Audio extends DoobieContentType(value = 4L, name = "audio")

}

case class DoobieContent(`type`: DoobieContentType)

sealed abstract class DoobieDrink(val value: Short, name: String) extends ShortEnumEntry

case object DoobieDrink extends ShortEnum[DoobieDrink] with ShortDoobieEnum[DoobieDrink] {

  case object OrangeJuice extends DoobieDrink(value = 1, name = "oj")
  case object AppleJuice  extends DoobieDrink(value = 2, name = "aj")
  case object Cola        extends DoobieDrink(value = 3, name = "cola")
  case object Beer        extends DoobieDrink(value = 4, name = "beer")

  override val values: immutable.IndexedSeq[DoobieDrink] = findValues

}

case class DoobieDrinkManufacturer(name: String, drink: DoobieDrink)

sealed abstract class DoobieOperatingSystem(val value: String) extends StringEnumEntry

case object DoobieOperatingSystem
    extends StringEnum[DoobieOperatingSystem]
    with StringDoobieEnum[DoobieOperatingSystem] {

  case object Linux   extends DoobieOperatingSystem("linux")
  case object OSX     extends DoobieOperatingSystem("osx")
  case object Windows extends DoobieOperatingSystem("windows")
  case object Android extends DoobieOperatingSystem("android")

  override val values: immutable.IndexedSeq[DoobieOperatingSystem] = findValues

}

case class DoobieComputer(operatingSystem: DoobieOperatingSystem)

sealed abstract class DoobieAlphabet(val value: Char) extends CharEnumEntry

case object DoobieAlphabet extends CharEnum[DoobieAlphabet] with CharDoobieEnum[DoobieAlphabet] {

  case object A extends DoobieAlphabet('A')
  case object B extends DoobieAlphabet('B')
  case object C extends DoobieAlphabet('C')
  case object D extends DoobieAlphabet('D')

  override val values: immutable.IndexedSeq[DoobieAlphabet] = findValues

}

case class DoobieName(name: String, initials: DoobieAlphabet)

sealed abstract class DoobieByte(val value: Byte) extends ByteEnumEntry

object DoobieByte extends ByteEnum[DoobieByte] with ByteDoobieEnum[DoobieByte] {
  override val values: immutable.IndexedSeq[DoobieByte] = findValues

  case object OneByte   extends DoobieByte(1)
  case object TwoByte   extends DoobieByte(2)
  case object ThreeByte extends DoobieByte(3)
  case object FourByte  extends DoobieByte(4)
}

case class DoobieChar(byte1: DoobieByte, byte2: DoobieByte)
