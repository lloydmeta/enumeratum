package enumeratum.values

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable

class QuillValueEnumSpec extends AnyFunSpec with Matchers {

  describe("An IntQuillEnum") {

    it("should encode to Int") {
      // we only need to test whether it can compile because Quill will fail compilation if an Encoder is not found
      """
import io.getquill._
val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
import ctx._
ctx.run(query[QuillBorrowerToLibraryItem].insert(_.borrower -> "Foo", _.item -> lift(QuillLibraryItem.Book: QuillLibraryItem)))
      """ should compile
    }

    it("should decode from Int") {
      // we only need to test whether it can compile because Quill will fail compilation if a Decoder is not found
      """
import io.getquill._
val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
import ctx._
ctx.run(query[QuillBorrowerToLibraryItem])
      """ should compile
    }

  }

  describe("A LongQuillEnum") {

    it("should encode to Long") {
      // we only need to test whether it can compile because Quill will fail compilation if an Encoder is not found
      """
import io.getquill._
val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
import ctx._
ctx.run(query[QuillContent].insert(_.`type` -> lift(QuillContentType.Image: QuillContentType)))
      """ should compile
    }

    it("should decode from Long") {
      // we only need to test whether it can compile because Quill will fail compilation if a Decoder is not found
      """
import io.getquill._
val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
import ctx._
ctx.run(query[QuillContent])
      """ should compile
    }

  }

  describe("A ShortQuillEnum") {

    it("should encode to Short") {
      // we only need to test whether it can compile because Quill will fail compilation if an Encoder is not found
      """
import io.getquill._
val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
import ctx._
ctx.run(query[QuillDrinkManufacturer].insert(_.name -> "Coca-Cola", _.drink -> lift(QuillDrink.Cola: QuillDrink)))
      """ should compile
    }

    it("should decode from Short") {
      // we only need to test whether it can compile because Quill will fail compilation if a Decoder is not found
      """
import io.getquill._
val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
import ctx._
ctx.run(query[QuillDrinkManufacturer])
      """ should compile
    }

  }

  describe("A StringQuillEnum") {

    it("should encode to String") {
      // we only need to test whether it can compile because Quill will fail compilation if an Encoder is not found
      """
import io.getquill._
val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
import ctx._
ctx.run(query[QuillComputer].insert(_.operatingSystem -> lift(QuillOperatingSystem.Windows: QuillOperatingSystem)))
      """ should compile
    }

    it("should decode from String") {
      // we only need to test whether it can compile because Quill will fail compilation if a Decoder is not found
      """
import io.getquill._
val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
import ctx._
ctx.run(query[QuillComputer])
      """ should compile
    }

  }

  describe("A CharQuillEnum") {

    it("should encode to Char") {
      // we only need to test whether it can compile because Quill will fail compilation if an Encoder is not found
      """
import io.getquill._
val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
import ctx._
ctx.run(query[QuillName].insert(_.name -> "Daniel", _.initials -> lift(QuillAlphabet.D: QuillAlphabet)))
      """ should compile
    }

    it("should decode from Char") {
      // we only need to test whether it can compile because Quill will fail compilation if a Decoder is not found
      """
import io.getquill._
val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
import ctx._
ctx.run(query[QuillName])
      """ should compile
    }

  }

  describe("A ByteQuillEnum") {

    it("should encode to Byte") {
      // we only need to test whether it can compile because Quill will fail compilation if an Encoder is not found
      """
import io.getquill._
val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
import ctx._
ctx.run(query[QuillChar].insert(_.byte1 -> lift(QuillByte.ThreeByte: QuillByte), _.byte2 -> lift(QuillByte.TwoByte: QuillByte)))
      """ should compile
    }

    it("should decode from Byte") {
      // we only need to test whether it can compile because Quill will fail compilation if a Decoder is not found
      """
import io.getquill._
val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
import ctx._
ctx.run(query[QuillChar])
      """ should compile
    }

  }

}

sealed abstract class QuillLibraryItem(val value: Int, val name: String) extends IntEnumEntry

case object QuillLibraryItem extends IntEnum[QuillLibraryItem] with IntQuillEnum[QuillLibraryItem] {

  // A good mix of named, unnamed, named + unordered args
  case object Book     extends QuillLibraryItem(value = 1, name = "book")
  case object Movie    extends QuillLibraryItem(name = "movie", value = 2)
  case object Magazine extends QuillLibraryItem(3, "magazine")
  case object CD       extends QuillLibraryItem(4, name = "cd")

  override val values: immutable.IndexedSeq[QuillLibraryItem] = findValues

}

case class QuillBorrowerToLibraryItem(borrower: String, item: QuillLibraryItem)

sealed abstract class QuillContentType(val value: Long, name: String) extends LongEnumEntry

case object QuillContentType
    extends LongEnum[QuillContentType]
    with LongQuillEnum[QuillContentType] {

  override val values: immutable.IndexedSeq[QuillContentType] = findValues

  case object Text  extends QuillContentType(value = 1L, name = "text")
  case object Image extends QuillContentType(value = 2L, name = "image")
  case object Video extends QuillContentType(value = 3L, name = "video")
  case object Audio extends QuillContentType(value = 4L, name = "audio")

}

case class QuillContent(`type`: QuillContentType)

sealed abstract class QuillDrink(val value: Short, name: String) extends ShortEnumEntry

case object QuillDrink extends ShortEnum[QuillDrink] with ShortQuillEnum[QuillDrink] {

  case object OrangeJuice extends QuillDrink(value = 1, name = "oj")
  case object AppleJuice  extends QuillDrink(value = 2, name = "aj")
  case object Cola        extends QuillDrink(value = 3, name = "cola")
  case object Beer        extends QuillDrink(value = 4, name = "beer")

  override val values: immutable.IndexedSeq[QuillDrink] = findValues

}

case class QuillDrinkManufacturer(name: String, drink: QuillDrink)

sealed abstract class QuillOperatingSystem(val value: String) extends StringEnumEntry

case object QuillOperatingSystem
    extends StringEnum[QuillOperatingSystem]
    with StringQuillEnum[QuillOperatingSystem] {

  case object Linux   extends QuillOperatingSystem("linux")
  case object OSX     extends QuillOperatingSystem("osx")
  case object Windows extends QuillOperatingSystem("windows")
  case object Android extends QuillOperatingSystem("android")

  override val values: immutable.IndexedSeq[QuillOperatingSystem] = findValues

}

case class QuillComputer(operatingSystem: QuillOperatingSystem)

sealed abstract class QuillAlphabet(val value: Char) extends CharEnumEntry

case object QuillAlphabet extends CharEnum[QuillAlphabet] with CharQuillEnum[QuillAlphabet] {

  case object A extends QuillAlphabet('A')
  case object B extends QuillAlphabet('B')
  case object C extends QuillAlphabet('C')
  case object D extends QuillAlphabet('D')

  override val values: immutable.IndexedSeq[QuillAlphabet] = findValues

}

case class QuillName(name: String, initials: QuillAlphabet)

sealed abstract class QuillByte(val value: Byte) extends ByteEnumEntry

object QuillByte extends ByteEnum[QuillByte] with ByteQuillEnum[QuillByte] {
  override val values: immutable.IndexedSeq[QuillByte] = findValues

  case object OneByte   extends QuillByte(1)
  case object TwoByte   extends QuillByte(2)
  case object ThreeByte extends QuillByte(3)
  case object FourByte  extends QuillByte(4)
}

case class QuillChar(byte1: QuillByte, byte2: QuillByte)
