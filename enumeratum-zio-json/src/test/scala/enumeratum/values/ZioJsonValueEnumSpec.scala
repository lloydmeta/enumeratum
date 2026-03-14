package enumeratum.values

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import zio.json._

class ZioJsonValueEnumSpec extends AnyFunSpec with Matchers {

  testZioJsonEnum("LongZioJsonEnum", ZioJsonContentType)
  testZioJsonEnum("ShortZioJsonEnum", ZioJsonDrinks)
  testZioJsonEnum("IntZioJsonEnum", ZioJsonLibraryItem)
  testZioJsonEnum("StringZioJsonEnum", ZioJsonOperatingSystem)
  testZioJsonKeyEnum("StringZioJsonEnum", ZioJsonOperatingSystem)
  testZioJsonKeyEnum("IntZioJsonEnum", ZioJsonLibraryItem)
  testZioJsonKeyEnum("LongZioJsonEnum", ZioJsonContentType)
  testZioJsonEnum("CharEnum", ZioJsonAlphabet)
  testZioJsonEnum("ByteEnum", ZioJsonBites)
  testZioJsonEnum("IntZioJsonEnum with val value members", ZioJsonMovieGenre)

  private def testZioJsonEnum[ValueType: JsonEncoder: JsonDecoder, EntryType <: ValueEnumEntry[
    ValueType
  ]: JsonEncoder: JsonDecoder](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType] with ZioJsonValueEnum[ValueType, EntryType]
  ): Unit = {
    describe(enumKind) {

      describe("to JSON") {
        it("should work") {
          myEnum.values.foreach { entry =>
            entry.toJson shouldBe entry.value.toJson
          }
        }
      }

      describe("from JSON") {
        it("should parse to members when given proper JSON") {
          myEnum.values.foreach { entry =>
            entry.value.toJson.fromJson[EntryType] shouldBe Right(entry)
          }
        }

        it("should fail to parse random JSON to members") {
          val failures = Seq(
            """"GOBBLYGOOKITY"""".fromJson[EntryType],
            Int.MaxValue.toJson.fromJson[EntryType]
          )
          failures.foreach { f =>
            f shouldBe a[Left[_, _]]
          }
        }
      }

    }
  }

  private def testZioJsonKeyEnum[
      ValueType: JsonFieldEncoder: JsonFieldDecoder,
      EntryType <: ValueEnumEntry[
        ValueType
      ]: JsonEncoder: JsonDecoder: JsonFieldEncoder: JsonFieldDecoder
  ](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType] with ZioJsonValueEnum[ValueType, EntryType]
  ): Unit = {
    describe(s"$enumKind as Key") {
      describe("to JSON") {
        it("should round-trip") {
          val map  = myEnum.values.zipWithIndex.map { case (e, i) => e -> i }.toMap
          val json = map.toJson
          json.fromJson[Map[EntryType, Int]] shouldBe Right(map)
        }
      }
    }
  }

}

sealed abstract class ZioJsonContentType(val value: Long, name: String) extends LongEnumEntry

case object ZioJsonContentType
    extends LongEnum[ZioJsonContentType]
    with LongZioJsonEnum[ZioJsonContentType] {

  val values = findValues

  case object Text  extends ZioJsonContentType(value = 1L, name = "text")
  case object Image extends ZioJsonContentType(value = 2L, name = "image")
  case object Video extends ZioJsonContentType(value = 3L, name = "video")
  case object Audio extends ZioJsonContentType(value = 4L, name = "audio")

}

sealed abstract class ZioJsonDrinks(val value: Short, name: String) extends ShortEnumEntry

case object ZioJsonDrinks extends ShortEnum[ZioJsonDrinks] with ShortZioJsonEnum[ZioJsonDrinks] {

  case object OrangeJuice extends ZioJsonDrinks(value = 1, name = "oj")
  case object AppleJuice  extends ZioJsonDrinks(value = 2, name = "aj")
  case object Cola        extends ZioJsonDrinks(value = 3, name = "cola")
  case object Beer        extends ZioJsonDrinks(value = 4, name = "beer")

  val values = findValues

}

sealed abstract class ZioJsonLibraryItem(val value: Int, val name: String) extends IntEnumEntry

case object ZioJsonLibraryItem
    extends IntEnum[ZioJsonLibraryItem]
    with IntZioJsonEnum[ZioJsonLibraryItem] {

  case object Book     extends ZioJsonLibraryItem(value = 1, name = "book")
  case object Movie    extends ZioJsonLibraryItem(name = "movie", value = 2)
  case object Magazine extends ZioJsonLibraryItem(3, "magazine")
  case object CD       extends ZioJsonLibraryItem(4, name = "cd")

  val values = findValues

}

sealed abstract class ZioJsonOperatingSystem(val value: String) extends StringEnumEntry

case object ZioJsonOperatingSystem
    extends StringEnum[ZioJsonOperatingSystem]
    with StringZioJsonEnum[ZioJsonOperatingSystem] {

  case object Linux   extends ZioJsonOperatingSystem("linux")
  case object OSX     extends ZioJsonOperatingSystem("osx")
  case object Windows extends ZioJsonOperatingSystem("windows")
  case object Android extends ZioJsonOperatingSystem("android")

  val values = findValues

}

sealed abstract class ZioJsonMovieGenre extends IntEnumEntry

case object ZioJsonMovieGenre
    extends IntEnum[ZioJsonMovieGenre]
    with IntZioJsonEnum[ZioJsonMovieGenre] {

  case object Action extends ZioJsonMovieGenre {
    val value = 1
  }
  case object Comedy extends ZioJsonMovieGenre {
    val value: Int = 2
  }
  case object Romance extends ZioJsonMovieGenre {
    val value = 3
  }

  val values = findValues

}

sealed abstract class ZioJsonAlphabet(val value: Char) extends CharEnumEntry

case object ZioJsonAlphabet
    extends CharEnum[ZioJsonAlphabet]
    with CharZioJsonEnum[ZioJsonAlphabet] {

  case object A extends ZioJsonAlphabet('A')
  case object B extends ZioJsonAlphabet('B')
  case object C extends ZioJsonAlphabet('C')
  case object D extends ZioJsonAlphabet('D')

  val values = findValues

}

sealed abstract class ZioJsonBites(val value: Byte) extends ByteEnumEntry

object ZioJsonBites extends ByteEnum[ZioJsonBites] with ByteZioJsonEnum[ZioJsonBites] {
  val values = findValues

  case object OneByte   extends ZioJsonBites(1)
  case object TwoByte   extends ZioJsonBites(2)
  case object ThreeByte extends ZioJsonBites(3)
  case object FourByte  extends ZioJsonBites(4)
}
