package enumeratum.values

import org.scalatest.{ FunSpec, Matchers }
import cats.data.Xor
import io.circe.{ Decoder, Encoder, Json }
import io.circe.syntax._

/**
 * Created by Lloyd on 4/14/16.
 *
 * Copyright 2016
 */
class CirceValueEnumSpec extends FunSpec with Matchers {

  testCirceEnum("LongCirceEnum", CirceContentType)
  testCirceEnum("ShortCirceEnum", CirceDrinks)
  testCirceEnum("IntCirceEnum", CirceLibraryItem)
  testCirceEnum("StringCirceEnum", CirceOperatingSystem)
  testCirceEnum("IntCirceEnum with val value members", CirceMovieGenre)

  // Test method that generates tests for most primitve-based ValueEnums when given a simple descriptor and the enum
  private def testCirceEnum[ValueType: Encoder: Decoder, EntryType <: ValueEnumEntry[ValueType]: Encoder: Decoder](enumKind: String, enum: ValueEnum[ValueType, EntryType] with CirceValueEnum[ValueType, EntryType]): Unit = {
    describe(enumKind) {

      describe("to JSON") {

        it("should work") {
          enum.values.foreach { entry =>
            entry.asJson shouldBe entry.value.asJson
          }
        }

      }

      describe("from Json") {

        it("should parse to members when given proper JSON") {
          enum.values.foreach { entry =>
            entry.value.asJson.as[EntryType] shouldBe Xor.Right(entry)
          }
        }

        it("should fail to parse random JSON to members") {
          Json.fromString("GOBBLYGOOKITY").as[EntryType].isLeft shouldBe true
          Json.fromInt(Int.MaxValue).as[EntryType].isLeft shouldBe true
        }

      }

    }
  }

}

sealed abstract class CirceContentType(val value: Long, name: String) extends LongEnumEntry

case object CirceContentType
    extends LongEnum[CirceContentType]
    with LongCirceEnum[CirceContentType] {

  val values = findValues

  case object Text extends CirceContentType(value = 1L, name = "text")
  case object Image extends CirceContentType(value = 2L, name = "image")
  case object Video extends CirceContentType(value = 3L, name = "video")
  case object Audio extends CirceContentType(value = 4L, name = "audio")

}

sealed abstract class CirceDrinks(val value: Short, name: String) extends ShortEnumEntry

case object CirceDrinks extends ShortEnum[CirceDrinks] with ShortCirceEnum[CirceDrinks] {

  case object OrangeJuice extends CirceDrinks(value = 1, name = "oj")
  case object AppleJuice extends CirceDrinks(value = 2, name = "aj")
  case object Cola extends CirceDrinks(value = 3, name = "cola")
  case object Beer extends CirceDrinks(value = 4, name = "beer")

  val values = findValues

}

sealed abstract class CirceLibraryItem(val value: Int, val name: String) extends IntEnumEntry

case object CirceLibraryItem extends IntEnum[CirceLibraryItem] with IntCirceEnum[CirceLibraryItem] {

  // A good mix of named, unnamed, named + unordered args
  case object Book extends CirceLibraryItem(value = 1, name = "book")
  case object Movie extends CirceLibraryItem(name = "movie", value = 2)
  case object Magazine extends CirceLibraryItem(3, "magazine")
  case object CD extends CirceLibraryItem(4, name = "cd")

  val values = findValues

}

sealed abstract class CirceOperatingSystem(val value: String) extends StringEnumEntry

case object CirceOperatingSystem extends StringEnum[CirceOperatingSystem] with StringCirceEnum[CirceOperatingSystem] {

  case object Linux extends CirceOperatingSystem("linux")
  case object OSX extends CirceOperatingSystem("osx")
  case object Windows extends CirceOperatingSystem("windows")
  case object Android extends CirceOperatingSystem("android")

  val values = findValues

}

sealed abstract class CirceMovieGenre extends IntEnumEntry

case object CirceMovieGenre extends IntEnum[CirceMovieGenre] with IntCirceEnum[CirceMovieGenre] {

  case object Action extends CirceMovieGenre {
    val value = 1
  }
  case object Comedy extends CirceMovieGenre {
    val value: Int = 2
  }
  case object Romance extends CirceMovieGenre {
    val value = 3
  }

  val values = findValues

}
