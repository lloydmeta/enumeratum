package enumeratum.values

import java.util.NoSuchElementException

import org.scalatest.{ FunSpec, Matchers }

/**
 * Created by Lloyd on 4/12/16.
 *
 * Copyright 2016
 */
class ValueEnumSpec extends FunSpec with Matchers {

  describe("IntEnum") {

    describe("withValue") {

      it("should return entries that match the value") {
        LibraryItem.withValue(1) shouldBe LibraryItem.Book
        LibraryItem.withValue(2) shouldBe LibraryItem.Movie
        LibraryItem.withValue(3) shouldBe LibraryItem.Magazine
        LibraryItem.withValue(4) shouldBe LibraryItem.CD
      }

      it("should throw on values that don't map to any entries") {
        intercept[NoSuchElementException] {
          LibraryItem.withValue(5)
        }
      }

    }

    describe("withValueOpt") {

      it("should return Some(entry) that match the value") {
        LibraryItem.withValueOpt(1) shouldBe Some(LibraryItem.Book)
        LibraryItem.withValueOpt(2) shouldBe Some(LibraryItem.Movie)
        LibraryItem.withValueOpt(3) shouldBe Some(LibraryItem.Magazine)
        LibraryItem.withValueOpt(4) shouldBe Some(LibraryItem.CD)
      }

      it("should return None when given values that do not map to any entries") {
        LibraryItem.withValueOpt(5) shouldBe None
      }

    }

    describe("indexOf") {

      it("should return the proper index for proper members") {
        LibraryItem.indexOf(LibraryItem.Book) shouldBe 0
      }
      it("should return -1 for non members") {
        LibraryItem.indexOf(Newspaper) shouldBe -1
      }

    }

  }

  describe("ShortEnum") {

    describe("withValue") {

      it("should return entries that match the value") {
        Drinks.withValue(1) shouldBe Drinks.OrangeJuice
        Drinks.withValue(2) shouldBe Drinks.AppleJuice
        Drinks.withValue(3) shouldBe Drinks.Cola
        Drinks.withValue(4) shouldBe Drinks.Beer
      }

      it("should throw on values that don't map to any entries") {
        intercept[NoSuchElementException] {
          LibraryItem.withValue(5)
        }
      }

    }

    describe("withValueOpt") {

      it("should return Some(entry) that match the value") {
        Drinks.withValueOpt(1) shouldBe Some(Drinks.OrangeJuice)
        Drinks.withValueOpt(2) shouldBe Some(Drinks.AppleJuice)
        Drinks.withValueOpt(3) shouldBe Some(Drinks.Cola)
        Drinks.withValueOpt(4) shouldBe Some(Drinks.Beer)
      }

      it("should return None when given values that do not map to any entries") {
        Drinks.withValueOpt(5) shouldBe None
      }

    }

    describe("indexOf") {

      it("should return the proper index for proper members") {
        Drinks.indexOf(Drinks.OrangeJuice) shouldBe 0
      }
      it("should return -1 for non members") {
        Drinks.indexOf(CoughSyrup) shouldBe -1
      }

    }

  }

  describe("LongEnum") {

    describe("withName") {

      it("should return entries that match the value") {
        ContentType.withValue(1) shouldBe ContentType.Text
        ContentType.withValue(2) shouldBe ContentType.Image
        ContentType.withValue(3) shouldBe ContentType.Video
        ContentType.withValue(4) shouldBe ContentType.Audio
      }

      it("should throw on values that don't map to any entries") {
        intercept[NoSuchElementException] {
          LibraryItem.withValue(5)
        }
      }

    }

    describe("withValueOpt") {

      it("should return Some(entry) that match the value") {
        ContentType.withValueOpt(1) shouldBe Some(ContentType.Text)
        ContentType.withValueOpt(2) shouldBe Some(ContentType.Image)
        ContentType.withValueOpt(3) shouldBe Some(ContentType.Video)
        ContentType.withValueOpt(4) shouldBe Some(ContentType.Audio)
      }

      it("should return None when given values that do not map to any entries") {
        ContentType.withValueOpt(5) shouldBe None
      }

    }

    describe("indexOf") {

      it("should return the proper index for proper members") {
        ContentType.indexOf(ContentType.Text) shouldBe 0
      }
      it("should return -1 for non members") {
        ContentType.indexOf(Papyrus) shouldBe -1
      }

    }

  }

  describe("should still work when using val members in the body") {

    describe("withValue") {

      it("should return entries that match the value") {
        MovieGenre.withValue(1) shouldBe MovieGenre.Action
        MovieGenre.withValue(2) shouldBe MovieGenre.Comedy
        MovieGenre.withValue(3) shouldBe MovieGenre.Romance
      }

      it("should throw on values that don't map to any entries") {
        intercept[NoSuchElementException] {
          MovieGenre.withValue(4)
        }
      }

    }

    describe("withValueOpt") {

      it("should return Some(entry) that match the value") {
        MovieGenre.withValueOpt(1) shouldBe Some(MovieGenre.Action)
        MovieGenre.withValueOpt(2) shouldBe Some(MovieGenre.Comedy)
        MovieGenre.withValueOpt(3) shouldBe Some(MovieGenre.Romance)
      }

      it("should return None when given values that do not map to any entries") {
        MovieGenre.withValueOpt(5) shouldBe None
      }

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