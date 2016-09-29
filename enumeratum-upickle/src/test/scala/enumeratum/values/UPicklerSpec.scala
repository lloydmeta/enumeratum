package enumeratum.values

import org.scalatest._
import upickle.Js
import upickle.default.{readJs, writeJs, Reader, Writer}

/**
  * Created by Lloyd on 4/14/16.
  *
  * Copyright 2016
  */
class UPicklerSpec extends FunSpec with Matchers {

  testPickling("LongUPickleEnum", UPickleContentType)
  testPickling("ShortUPickleEnum", UPickleDrinks)
  testPickling("IntUPickleEnum", UPickleLibraryItem)
  testPickling("StringUPickleEnum", UPickleOperatingSystem)
  testPickling("ByteUPickleEnum", UPickleBites)
  testPickling("CharUPickleEnum", UPickleAlphabet)
  testPickling("IntUPickleEnum with values declared as members", UPickleMovieGenre)

  /**
    * Given an enum, tests its JSON reading and writing behaviour, grouping the test results under the given enumKind descriptor
    */
  private def testPickling[ValueType: Writer,
                           EntryType <: ValueEnumEntry[ValueType]: Reader: Writer](
      enumKind: String,
      enum: UPickleValueEnum[ValueType, EntryType] with ValueEnum[ValueType, EntryType]) = {
    describe(enumKind) {
      describe("Reader") {

        it("should work with valid values") {
          enum.values.foreach { entry =>
            val written = writeJs(entry)
            readJs(written) shouldBe entry
          }
        }

        it("should fail with invalid values") {
          intercept[Exception] {
            readJs(Js.Str("ababab"))
          }
          intercept[Exception] {
            readJs(Js.Num(Int.MaxValue))
          }
        }

      }

      describe("Writer") {

        it("should write enum values to JS") {
          val valueTypeWriter = implicitly[Writer[ValueType]]
          enum.values.foreach { entry =>
            writeJs(entry) shouldBe valueTypeWriter.write(entry.value)
          }
        }

      }
    }
  }

}

sealed abstract class UPickleContentType(val value: Long, name: String) extends LongEnumEntry

case object UPickleContentType
    extends LongEnum[UPickleContentType]
    with LongUPickleEnum[UPickleContentType] {

  val values = findValues

  case object Text  extends UPickleContentType(value = 1L, name = "text")
  case object Image extends UPickleContentType(value = 2L, name = "image")
  case object Video extends UPickleContentType(value = 3L, name = "video")
  case object Audio extends UPickleContentType(value = 4L, name = "audio")

}

sealed abstract class UPickleDrinks(val value: Short, name: String) extends ShortEnumEntry

case object UPickleDrinks extends ShortEnum[UPickleDrinks] with ShortUPickleEnum[UPickleDrinks] {

  case object OrangeJuice extends UPickleDrinks(value = 1, name = "oj")
  case object AppleJuice  extends UPickleDrinks(value = 2, name = "aj")
  case object Cola        extends UPickleDrinks(value = 3, name = "cola")
  case object Beer        extends UPickleDrinks(value = 4, name = "beer")

  val values = findValues

}

sealed abstract class UPickleLibraryItem(val value: Int, val name: String) extends IntEnumEntry

case object UPickleLibraryItem
    extends IntEnum[UPickleLibraryItem]
    with IntUPickleEnum[UPickleLibraryItem] {

  // A good mix of named, unnamed, named + unordered args
  case object Book     extends UPickleLibraryItem(value = 1, name = "book")
  case object Movie    extends UPickleLibraryItem(name = "movie", value = 2)
  case object Magazine extends UPickleLibraryItem(3, "magazine")
  case object CD       extends UPickleLibraryItem(4, name = "cd")

  val values = findValues

}

sealed abstract class UPickleOperatingSystem(val value: String) extends StringEnumEntry

case object UPickleOperatingSystem
    extends StringEnum[UPickleOperatingSystem]
    with StringUPickleEnum[UPickleOperatingSystem] {

  case object Linux   extends UPickleOperatingSystem("linux")
  case object OSX     extends UPickleOperatingSystem("osx")
  case object Windows extends UPickleOperatingSystem("windows")
  case object Android extends UPickleOperatingSystem("android")

  val values = findValues

}

sealed abstract class UPickleMovieGenre extends IntEnumEntry

case object UPickleMovieGenre
    extends IntEnum[UPickleMovieGenre]
    with IntUPickleEnum[UPickleMovieGenre] {

  case object Action extends UPickleMovieGenre {
    val value = 1
  }
  case object Comedy extends UPickleMovieGenre {
    val value: Int = 2
  }
  case object Romance extends UPickleMovieGenre {
    val value = 3
  }

  val values = findValues

}

sealed abstract class UPickleAlphabet(val value: Char) extends CharEnumEntry

case object UPickleAlphabet
    extends CharEnum[UPickleAlphabet]
    with CharUPickleEnum[UPickleAlphabet] {

  case object A extends UPickleAlphabet('A')
  case object B extends UPickleAlphabet('B')
  case object C extends UPickleAlphabet('C')
  case object D extends UPickleAlphabet('D')

  val values = findValues

}

sealed abstract class UPickleBites(val value: Byte) extends ByteEnumEntry

object UPickleBites extends ByteEnum[UPickleBites] with ByteUPickleEnum[UPickleBites] {
  val values = findValues

  case object OneByte   extends UPickleBites(1)
  case object TwoByte   extends UPickleBites(2)
  case object ThreeByte extends UPickleBites(3)
  case object FourByte  extends UPickleBites(4)
}
