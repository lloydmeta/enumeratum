package enumeratum.values

import java.util.NoSuchElementException

import org.scalatest._

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */
trait ValueEnumHelpers { this: FunSpec with Matchers =>

  def testEnum[EntryType <: ValueEnumEntry[ValueType], ValueType <: AnyVal: Numeric](enumKind: String, enum: ValueEnum[ValueType, EntryType]): Unit = {
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
