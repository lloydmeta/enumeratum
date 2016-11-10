package enumeratum.values

import org.scalatest._
import play.api.libs.json._
import org.scalatest.OptionValues._

/**
  * Created by Lloyd on 4/13/16.
  *
  * Copyright 2016
  */
trait EnumJsonFormatHelpers { this: FunSpec with Matchers =>

  def testNumericWrites[EntryType <: ValueEnumEntry[ValueType],
                        ValueType <: AnyVal: Numeric: Writes](
      enumKind: String,
      enum: ValueEnum[ValueType, EntryType],
      providedWrites: Option[Writes[EntryType]] = None
  ): Unit = {
    val numeric = implicitly[Numeric[ValueType]]
    testWrites(enumKind, enum, { i: ValueType =>
      JsNumber(numeric.toInt(i))
    }, providedWrites)
  }

  def testWrites[EntryType <: ValueEnumEntry[ValueType], ValueType: Writes](
      enumKind: String,
      enum: ValueEnum[ValueType, EntryType],
      jsWrapper: ValueType => JsValue,
      providedWrites: Option[Writes[EntryType]] = None
  ): Unit = {
    val writes = providedWrites.getOrElse(EnumFormats.writes(enum))
    describe(enumKind) {
      it("should write proper JsValues") {
        enum.values.foreach { entry =>
          writes.writes(entry) shouldBe jsWrapper(entry.value)
        }
      }
    }
  }

  def testNumericReads[EntryType <: ValueEnumEntry[ValueType],
                       ValueType <: AnyVal: Numeric: Reads](
      enumKind: String,
      enum: ValueEnum[ValueType, EntryType],
      providedReads: Option[Reads[EntryType]] = None
  ): Unit = {
    val numeric = implicitly[Numeric[ValueType]]
    testReads(enumKind, enum, { i: ValueType =>
      JsNumber(numeric.toInt(i))
    }, providedReads)
  }

  def testReads[EntryType <: ValueEnumEntry[ValueType], ValueType: Reads](
      enumKind: String,
      enum: ValueEnum[ValueType, EntryType],
      jsWrapper: ValueType => JsValue,
      providedReads: Option[Reads[EntryType]] = None
  ): Unit = {
    val reads = providedReads.getOrElse(EnumFormats.reads(enum))
    describe(enumKind) {
      it("should read valid values") {
        enum.values.foreach { entry =>
          reads.reads(jsWrapper(entry.value)).asOpt.value shouldBe entry
        }
      }
      it("should fail to read with invalid values") {
        reads.reads(JsNumber(Int.MaxValue)) shouldBe 'error
        reads.reads(JsString("boon")) shouldBe 'error
      }
    }
  }

  def testNumericFormats[EntryType <: ValueEnumEntry[ValueType],
                         ValueType <: AnyVal: Numeric: Reads: Writes](
      enumKind: String,
      enum: ValueEnum[ValueType, EntryType],
      providedFormat: Option[Format[EntryType]] = None
  ): Unit = {
    testNumericReads(enumKind, enum, providedFormat)
    testNumericWrites(enumKind, enum, providedFormat)
  }

  def testFormats[EntryType <: ValueEnumEntry[ValueType], ValueType: Reads: Writes](
      enumKind: String,
      enum: ValueEnum[ValueType, EntryType],
      jsWrapper: ValueType => JsValue,
      providedFormat: Option[Format[EntryType]] = None
  ): Unit = {
    val format = providedFormat.getOrElse(EnumFormats.formats(enum))
    testReads(enumKind, enum, jsWrapper, Some(format))
    testWrites(enumKind, enum, jsWrapper, Some(format))
  }

}
