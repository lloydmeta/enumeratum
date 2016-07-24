package enumeratum.values

import org.scalatest.{ FunSpec, Matchers }

/**
 * Created by Lloyd on 4/12/16.
 *
 * Copyright 2016
 */
class ValueEnumSpec extends FunSpec with Matchers with ValueEnumHelpers {

  describe("basic sanity check") {

    it("should have the proper values") {
      LibraryItem.withValue(1) shouldBe LibraryItem.Book
      LibraryItem.withValue(2) shouldBe LibraryItem.Movie
      LibraryItem.withValue(10) shouldBe LibraryItem.Magazine
      LibraryItem.withValue(14) shouldBe LibraryItem.CD
    }

  }

  testEnum("IntEnum", LibraryItem)
  testEnum("ShortEnum", Drinks)
  testEnum("LongEnum", ContentType)
  testEnum("when using val members in the body", MovieGenre)

  describe("finding companion object") {

    it("should work for IntEnums") {
      def findCompanion[EntryType <: IntEnumEntry: IntEnum](entry: EntryType) = implicitly[IntEnum[EntryType]]
      val companion = findCompanion(LibraryItem.Magazine: LibraryItem)
      companion shouldBe LibraryItem
      companion.values should contain(LibraryItem.Magazine)
    }

    it("should work for ShortEnum") {
      def findCompanion[EntryType <: ShortEnumEntry: ShortEnum](entry: EntryType) = implicitly[ShortEnum[EntryType]]
      val companion = findCompanion(Drinks.Beer: Drinks)
      companion shouldBe Drinks
      companion.values should contain(Drinks.Cola)
    }

    it("should work for LongEnum") {
      def findCompanion[EntryType <: LongEnumEntry: LongEnum](entry: EntryType) = implicitly[LongEnum[EntryType]]
      val companion = findCompanion(ContentType.Image: ContentType)
      companion shouldBe ContentType
      companion.values should contain(ContentType.Audio)
    }

  }

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

}