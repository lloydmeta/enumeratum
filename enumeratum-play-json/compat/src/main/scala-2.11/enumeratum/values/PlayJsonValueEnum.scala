package enumeratum.values

import play.api.libs.json.Format

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */

trait PlayJsonValueEnum[ValueType <: AnyVal, EntryType <: ValueEnumEntry[ValueType]] { enum: ValueEnum[ValueType, EntryType] =>

  /**
   * Implicit JSON format for the entries of this enum
   */
  implicit def format: Format[EntryType]

}

/**
 * Enum implementation for Int enum members that contains an implicit Play JSON Format
 */
trait IntPlayJsonValueEnum[EntryType <: IntEnumEntry] extends PlayJsonValueEnum[Int, EntryType] { this: IntEnum[EntryType] =>
  implicit val format: Format[EntryType] = EnumFormats.formats(this)
}

/**
 * Enum implementation for Long enum members that contains an implicit Play JSON Format
 */
trait LongPlayJsonValueEnum[EntryType <: LongEnumEntry] extends PlayJsonValueEnum[Long, EntryType] { this: LongEnum[EntryType] =>
  implicit val format: Format[EntryType] = EnumFormats.formats(this)
}

/**
 * Enum implementation for Short enum members that contains an implicit Play JSON Format
 */
trait ShortPlayJsonValueEnum[EntryType <: ShortEnumEntry] extends PlayJsonValueEnum[Short, EntryType] { this: ShortEnum[EntryType] =>
  implicit val format: Format[EntryType] = EnumFormats.formats(this)
}