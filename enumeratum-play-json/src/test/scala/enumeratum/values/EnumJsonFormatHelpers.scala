package enumeratum.values

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._
import org.scalatest.OptionValues._

/** Created by Lloyd on 4/13/16.
  *
  * Copyright 2016
  */
trait EnumJsonFormatHelpers { this: AnyFunSpec with Matchers =>

  def testNumericWrites[EntryType <: ValueEnumEntry[
    ValueType
  ], ValueType <: AnyVal: Numeric: Writes](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType],
      providedWrites: Option[Writes[EntryType]] = None
  ): Unit = {
    val numeric = implicitly[Numeric[ValueType]]
    testWrites(
      enumKind,
      myEnum,
      { (i: ValueType) =>
        JsNumber(numeric.toInt(i))
      },
      providedWrites
    )
  }

  def testWrites[EntryType <: ValueEnumEntry[ValueType], ValueType: Writes](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType],
      jsWrapper: ValueType => JsValue,
      providedWrites: Option[Writes[EntryType]] = None
  ): Unit = {
    val writes = providedWrites.getOrElse(EnumFormats.writes(myEnum))
    describe(enumKind) {
      it("should write proper JsValues") {
        myEnum.values.foreach { entry =>
          writes.writes(entry) shouldBe jsWrapper(entry.value)
        }
      }
    }
  }

  def testNumericReads[EntryType <: ValueEnumEntry[ValueType], ValueType <: AnyVal: Numeric: Reads](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType],
      providedReads: Option[Reads[EntryType]] = None
  ): Unit = {
    val numeric = implicitly[Numeric[ValueType]]
    testReads(
      enumKind,
      myEnum,
      { (i: ValueType) =>
        JsNumber(numeric.toInt(i))
      },
      providedReads
    )
  }

  def testReads[EntryType <: ValueEnumEntry[ValueType], ValueType: Reads](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType],
      jsWrapper: ValueType => JsValue,
      providedReads: Option[Reads[EntryType]] = None
  ): Unit = {
    val reads = providedReads.getOrElse(EnumFormats.reads(myEnum))
    describe(enumKind) {
      it("should read valid values") {
        myEnum.values.foreach { entry =>
          reads.reads(jsWrapper(entry.value)).asOpt.value shouldBe entry
        }
      }

      it("should fail to read with invalid values") {
        reads.reads(JsNumber(Int.MaxValue)).isError shouldBe true
        reads.reads(JsString("boon")).isError shouldBe true
      }
    }
  }

  def testNumericFormats[EntryType <: ValueEnumEntry[
    ValueType
  ], ValueType <: AnyVal: Numeric: Reads: Writes](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType],
      providedFormat: Option[Format[EntryType]] = None
  ): Unit = {
    testNumericReads(enumKind, myEnum, providedFormat)
    testNumericWrites(enumKind, myEnum, providedFormat)
  }

  def testFormats[EntryType <: ValueEnumEntry[ValueType], ValueType: Reads: Writes](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType],
      jsWrapper: ValueType => JsValue,
      providedFormat: Option[Format[EntryType]] = None
  ): Unit = {
    val format = providedFormat.getOrElse(EnumFormats.formats(myEnum))
    testReads(enumKind, myEnum, jsWrapper, Some(format))
    testWrites(enumKind, myEnum, jsWrapper, Some(format))
  }

}
