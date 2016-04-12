package enumeratum.values

import java.util.NoSuchElementException

import org.scalatest.{FunSpec, Matchers}

/**
  * Created by Lloyd on 4/12/16.
  *
  * Copyright 2016
  */
class ValueEnumSpec extends FunSpec with Matchers {

  describe("withName") {

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