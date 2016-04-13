package enumeratum.values

import org.scalatest.OptionValues._
import org.scalatest._
import play.api.libs.json.{ JsNumber, JsString, Reads, Writes }

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */
class EnumFormatsSpec extends FunSpec with Matchers {

  describe(".reads") {

    testReads("IntEnum", LibraryItem)
    testReads("LongEnum", ContentType)
    testReads("ShortEnum", Drinks)
  }

  describe(".writes") {

    testWrites("IntEnum", LibraryItem)
    testWrites("LongEnum", ContentType)
    testWrites("ShortEnum", Drinks)

  }

  describe(".formats") {

    testFormats("IntEnum", LibraryItem)
    testFormats("LongEnum", ContentType)
    testFormats("ShortEnum", Drinks)

  }

  def testWrites[EntryType <: ValueEnumEntry[ValueType], ValueType <: AnyVal: Numeric: Writes](enumKind: String, enum: ValueEnum[EntryType, ValueType], providedWrites: Option[Writes[EntryType]] = None): Unit = {
    val numeric = implicitly[Numeric[ValueType]]
    val writes = providedWrites.getOrElse(EnumFormats.writes(enum))
    describe(s"$enumKind") {
      it("should write proper JsValues") {
        writes.writes(enum.values.head) shouldBe JsNumber(numeric.toInt(enum.values.head.value))
      }
    }
  }

  def testReads[EntryType <: ValueEnumEntry[ValueType], ValueType <: AnyVal: Numeric: Reads](enumKind: String, enum: ValueEnum[EntryType, ValueType], providedReads: Option[Reads[EntryType]] = None): Unit = {
    val numeric = implicitly[Numeric[ValueType]]
    val reads = providedReads.getOrElse(EnumFormats.reads(enum))
    describe(s"$enumKind") {
      it("should read valid values") {
        reads.reads(JsNumber(numeric.toInt(enum.values.head.value))).asOpt.value shouldBe enum.values.head
        reads.reads(JsNumber(numeric.toInt(enum.values(2).value))).asOpt.value shouldBe enum.values(2)
      }
      it("should fail to read with invalid values") {
        reads.reads(JsNumber(10)) shouldBe 'error
        reads.reads(JsString("boon")) shouldBe 'error
      }
    }
  }

  def testFormats[EntryType <: ValueEnumEntry[ValueType], ValueType <: AnyVal: Numeric: Reads: Writes](enumKind: String, enum: ValueEnum[EntryType, ValueType]): Unit = {
    val format = EnumFormats.formats(enum)
    testReads(enumKind, enum, Some(format))
    testWrites(enumKind, enum, Some(format))
  }

}