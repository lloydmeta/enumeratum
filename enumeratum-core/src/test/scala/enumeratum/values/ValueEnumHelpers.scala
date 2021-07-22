package enumeratum.values

import java.util.NoSuchElementException

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Created by Lloyd on 4/13/16.
  *
  * Copyright 2016
  */
trait ValueEnumHelpers { this: AnyFunSpec with Matchers =>

  /*
   * Generates tests for a given enum and groups the tests inside the given enumKind descriptor
   */
  def testNumericEnum[EntryType <: ValueEnumEntry[ValueType], ValueType <: AnyVal: Numeric](
      enumKind: String,
      enum: ValueEnum[ValueType, EntryType]
  ): Unit = {
    val numeric = implicitly[Numeric[ValueType]]
    testEnum(enumKind, enum, Seq(numeric.fromInt(Int.MaxValue)))
  }

  /*
   * Generates tests for a given enum and groups the tests inside the given enumKind descriptor
   */
  def testEnum[EntryType <: ValueEnumEntry[ValueType], ValueType](
      enumKind: String,
      enum: ValueEnum[ValueType, EntryType],
      invalidValues: Seq[ValueType]
  ): Unit = {

    describe(enumKind) {

      it("should have more than one value (sanity test)") {
        enum.values.size should be > 0
      }

      describe("withValue") {

        it("should return entries that match the value") {
          enum.values.foreach { entry =>
            enum.withValue(entry.value) shouldBe entry
          }
        }

        it("should throw on values that don't map to any entries") {
          invalidValues.foreach { invalid =>
            intercept[NoSuchElementException] {
              enum.withValue(invalid)
            }
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
          invalidValues.foreach { invalid =>
            enum.withValueOpt(invalid) shouldBe None
          }
        }

      }

      describe("withValueEither") {

        it("should return Right(entry) that match the value") {
          enum.values.foreach { entry =>
            enum.withValueEither(entry.value) shouldBe Right(entry)
          }
        }

        it("should return Left when given values that do not map to any entries") {
          invalidValues.foreach { invalid =>
            enum.withValueEither(invalid) shouldBe Left(NoSuchMember(invalid, enum.values))
          }
        }

      }

      describe("in") {

        it("should return false if given an empty list") {
          enum.values.foreach { entry =>
            entry.in(Nil) shouldBe false
          }
        }

        it("should return false if given a list that does not hold the entry") {
          enum.values.foreach { entry =>
            entry.in(enum.values.filterNot(_ == entry)) shouldBe false
          }
        }

        it("should return true if the list only holds itself") {
          enum.values.foreach { entry =>
            entry.in(entry) shouldBe true
          }
        }

        it("should return true if given a list that has the current entry") {
          enum.values.foreach { entry =>
            entry.in(enum.values) shouldBe true
          }
        }
      }

    }
  }

}
