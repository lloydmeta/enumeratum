package enumeratum.values

import play.api.data.format.{Formats, Formatter}
import play.api.data.Mapping

/**
  * Created by Lloyd on 4/13/16.
  *
  * Copyright 2016
  */
sealed trait PlayFormValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {
  enum: ValueEnum[ValueType, EntryType] =>

  /**
    * The [[Formatter]] for binding the ValueType of this ValueEnum.
    *
    * Used for building the [[Formatter]] for the entries
    */
  protected def baseFormatter: Formatter[ValueType]

  /**
    * Field for mapping this enum in Forms
    */
  lazy val formField: Mapping[EntryType] = Forms.enum(baseFormatter)(enum)

}

/**
  * Form Bindable implicits for IntEnum
  */
trait IntPlayFormValueEnum[EntryType <: IntEnumEntry] extends PlayFormValueEnum[Int, EntryType] {
  this: IntEnum[EntryType] =>
  protected val baseFormatter: Formatter[Int] = Formats.intFormat
}

/**
  * Form Bindable implicits for LongEnum
  */
trait LongPlayFormValueEnum[EntryType <: LongEnumEntry] extends PlayFormValueEnum[Long, EntryType] {
  this: LongEnum[EntryType] =>
  protected val baseFormatter: Formatter[Long] = Formats.longFormat
}

/**
  * Form Bindable implicits for ShortEnum
  */
trait ShortPlayFormValueEnum[EntryType <: ShortEnumEntry]
    extends PlayFormValueEnum[Short, EntryType] { this: ShortEnum[EntryType] =>
  protected val baseFormatter: Formatter[Short] = Formats.shortFormat
}

/**
  * Form Bindable implicits for StringEnum
  */
trait StringPlayFormValueEnum[EntryType <: StringEnumEntry]
    extends PlayFormValueEnum[String, EntryType] { this: StringEnum[EntryType] =>
  protected val baseFormatter: Formatter[String] = Formats.stringFormat
}

/**
  * Form Bindable implicits for CharEnum
  */
trait CharPlayFormValueEnum[EntryType <: CharEnumEntry] extends PlayFormValueEnum[Char, EntryType] {
  this: CharEnum[EntryType] =>
  protected val baseFormatter: Formatter[Char] = Forms.charFormatter
}

/**
  * Form Bindable implicits for ByteEnum
  */
trait BytePlayFormValueEnum[EntryType <: ByteEnumEntry] extends PlayFormValueEnum[Byte, EntryType] {
  this: ByteEnum[EntryType] =>
  protected val baseFormatter: Formatter[Byte] = Formats.byteFormat
}
