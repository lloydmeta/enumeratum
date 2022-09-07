package enumeratum.values

import play.api.libs.json._
import EnumFormats.charFormat

/** Created by Lloyd on 4/13/16.
  *
  * Copyright 2016
  */
trait PlayJsonValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {
  _enum: ValueEnum[ValueType, EntryType] =>

  /** Implicit JSON format for the entries of this enum
    */
  implicit def format: Format[EntryType]

}

/** Enum implementation for Int enum members that contains an implicit Play JSON Format
  */
trait IntPlayJsonValueEnum[EntryType <: IntEnumEntry] extends PlayJsonValueEnum[Int, EntryType] {
  self: IntEnum[EntryType] =>
  implicit val format: Format[EntryType] = EnumFormats.formats(self)
}

/** Enum implementation for Long enum members that contains an implicit Play JSON Format
  */
trait LongPlayJsonValueEnum[EntryType <: LongEnumEntry] extends PlayJsonValueEnum[Long, EntryType] {
  self: LongEnum[EntryType] =>
  implicit val format: Format[EntryType] = EnumFormats.formats(self)
}

/** Enum implementation for Short enum members that contains an implicit Play JSON Format
  */
trait ShortPlayJsonValueEnum[EntryType <: ShortEnumEntry]
    extends PlayJsonValueEnum[Short, EntryType] { self: ShortEnum[EntryType] =>
  implicit val format: Format[EntryType] = EnumFormats.formats(self)
}

/** Enum implementation for String enum members that contains an implicit Play JSON Format
  */
trait StringPlayJsonValueEnum[EntryType <: StringEnumEntry]
    extends PlayJsonValueEnum[String, EntryType] { self: StringEnum[EntryType] =>
  implicit val format: Format[EntryType] = EnumFormats.formats(self)
}

/** Enum implementation for Char enum members that contains an implicit Play JSON Format
  */
trait CharPlayJsonValueEnum[EntryType <: CharEnumEntry] extends PlayJsonValueEnum[Char, EntryType] {
  self: CharEnum[EntryType] =>
  implicit val format: Format[EntryType] = EnumFormats.formats(self)
}

/** Enum implementation for Byte enum members that contains an implicit Play JSON Format
  */
trait BytePlayJsonValueEnum[EntryType <: ByteEnumEntry] extends PlayJsonValueEnum[Byte, EntryType] {
  self: ByteEnum[EntryType] =>
  implicit val format: Format[EntryType] = EnumFormats.formats(self)
}
