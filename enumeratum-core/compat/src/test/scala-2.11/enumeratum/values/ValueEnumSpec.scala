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
}