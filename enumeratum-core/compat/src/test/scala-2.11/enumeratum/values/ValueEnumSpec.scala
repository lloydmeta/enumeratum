package enumeratum.values

import java.util.NoSuchElementException

import org.scalatest.{ FunSpec, Matchers }

/**
 * Created by Lloyd on 4/12/16.
 *
 * Copyright 2016
 */
class ValueEnumSpec extends FunSpec with Matchers {

  testEnum("IntEnum", LibraryItem)
  testEnum("ShortEnum", Drinks)
  testEnum("LongEnum", ContentType)
  testEnum("when using val members in the body", MovieGenre)

  describe("compilation failures") {

    describe("problematic values") {

      it("should fail to compile when values are repeated") {
        """
        sealed abstract class ContentTypeRepeated(val value: Long, name: String) extends LongEnumEntry

        case object ContentTypeRepeated extends LongEnum[ContentTypeRepeated] {

          case object Text extends ContentTypeRepeated(value = 1L, name = "text")
          case object Image extends ContentTypeRepeated(value = 2L, name = "image")
          case object Video extends ContentTypeRepeated(value = 2L, name = "video")
          case object Audio extends ContentTypeRepeated(value = 4L, name = "audio")

          val values = findValues

        }
       """ shouldNot compile
      }

      it("should fail to compile when there are non literal values") {
        """
        sealed abstract class ContentTypeRepeated(val value: Long, name: String) extends LongEnumEntry

        case object ContentTypeRepeated extends LongEnum[ContentTypeRepeated] {
          val one = 1L

          case object Text extends ContentTypeRepeated(value = one, name = "text")
          case object Image extends ContentTypeRepeated(value = 2L, name = "image")
          case object Video extends ContentTypeRepeated(value = 2L, name = "video")
          case object Audio extends ContentTypeRepeated(value = 4L, name = "audio")

          val values = findValues

        }
        """ shouldNot compile
      }

    }

    describe("trying to use with improper types") {

      it("should fail to compile for unsealed traits") {
        """
        trait NotSealed extends IntEnumEntry

        object NotSealed extends IntEnum[NotSealed] {
          val values = findValues
        }
        """ shouldNot compile
      }

      it("should fail to compile for unsealed abstract classes") {
        """
         abstract class NotSealed(val value: Int) extends IntEnumEntry

         object NotSealed extends IntEnum[NotSealed] {
           val values = findValues
         }
        """ shouldNot compile
      }

      it("should fail to compile for classes") {
        """
        class Class(val value: Int) extends IntEnumEntry

        object Class extends IntEnum[Class] {
          val values = findValues
        }
        """ shouldNot compile
      }

      it("should fail to compile if the enum is not an object") {
        """
        sealed abstract class Sealed(val value: Int) extends IntEnumEntry

        class SealedEnum extends IntEnum[Sealed] {
          val values = findValues
        }
        """ shouldNot compile
      }
    }

  }

  def testEnum[EntryType <: ValueEnumEntry[ValueType], ValueType <: AnyVal: Numeric](enumKind: String, enum: ValueEnum[EntryType, ValueType]): Unit = {
    val numeric = implicitly[Numeric[ValueType]]
    describe(enumKind) {

      describe("withValue") {

        it("should return entries that match the value") {
          enum.values.foreach { entry =>
            enum.withValue(entry.value) shouldBe entry
          }
        }

        it("should throw on values that don't map to any entries") {
          intercept[NoSuchElementException] {
            enum.withValue(numeric.fromInt(Int.MaxValue))
          }
        }

      }

      describe("withValueOpt") {

        it("should return Some(entry) that match the value") {
          enum.values.foreach { entry =>
            enum.withValueOpt(entry.value) shouldBe Some(entry)
          }
        }

        it("should return None when given values that do not map to any entries") {
          enum.withValueOpt(numeric.fromInt(Int.MaxValue)) shouldBe None
        }

      }

    }
  }

}