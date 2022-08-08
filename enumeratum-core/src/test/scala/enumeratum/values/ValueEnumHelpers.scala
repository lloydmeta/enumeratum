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
      myEnum: ValueEnum[ValueType, EntryType]
  ): Unit = {
    val numeric = implicitly[Numeric[ValueType]]
    testEnum(enumKind, myEnum, Seq(numeric.fromInt(Int.MaxValue)))
  }

  /*
   * Generates tests for a given enum and groups the tests inside the given enumKind descriptor
   */
  def testEnum[EntryType <: ValueEnumEntry[ValueType], ValueType](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType],
      invalidValues: Seq[ValueType]
  ): Unit = {

    describe(enumKind) {

      it("should have more than one value (sanity test)") {
        myEnum.values.size should be > 0
      }

      describe("withValue") {

        it("should return entries that match the value") {
          myEnum.values.foreach { entry =>
            myEnum.withValue(entry.value) shouldBe entry
          }
        }

        it("should throw on values that don't map to any entries") {
          invalidValues.foreach { invalid =>
            intercept[NoSuchElementException] {
              myEnum.withValue(invalid)
            }
          }
        }

      }

      describe("withValueOpt") {

        it("should return Some(entry) that match the value") {
          myEnum.values.foreach { entry =>
            myEnum.withValueOpt(entry.value) shouldBe Some(entry)
          }
        }

        it("should return None when given values that do not map to any entries") {
          invalidValues.foreach { invalid =>
            myEnum.withValueOpt(invalid) shouldBe None
          }
        }

      }

      describe("withValueEither") {

        it("should return Right(entry) that match the value") {
          myEnum.values.foreach { entry =>
            myEnum.withValueEither(entry.value) shouldBe Right(entry)
          }
        }

        it("should return Left when given values that do not map to any entries") {
          invalidValues.foreach { invalid =>
            myEnum.withValueEither(invalid) shouldBe Left(NoSuchMember(invalid, myEnum.values))
          }
        }

      }

      describe("in") {

        it("should return false if given an empty list") {
          myEnum.values.foreach { entry =>
            entry.in(Nil) shouldBe false
          }
        }

        it("should return false if given a list that does not hold the entry") {
          myEnum.values.foreach { entry =>
            entry.in(myEnum.values.filterNot(_ == entry)) shouldBe false
          }
        }

        it("should return true if the list only holds itself") {
          myEnum.values.foreach { entry =>
            entry.in(entry) shouldBe true
          }
        }

        it("should return true if given a list that has the current entry") {
          myEnum.values.foreach { entry =>
            entry.in(myEnum.values) shouldBe true
          }
        }
      }

    }
  }

}
