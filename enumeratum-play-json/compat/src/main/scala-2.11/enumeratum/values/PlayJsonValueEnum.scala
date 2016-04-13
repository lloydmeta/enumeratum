package enumeratum.values

import play.api.libs.json.Format

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */

trait PlayJsonValueEnum[ValueType <: AnyVal, EntryType <: ValueEnumEntry[ValueType]] { enum: ValueEnum[EntryType, ValueType] =>

  /**
   * Implicit path binder for Play's default router
   */
  implicit def format: Format[EntryType]

}

/**
 * Json format implicits for IntEnum
 */
trait PlayJsonIntValeEnum[EntryType <: IntEnumEntry] extends PlayJsonValueEnum[Int, EntryType] { this: IntEnum[EntryType] =>
  implicit val format: Format[EntryType] = EnumFormats.formats(this)
}

/**
 * Json format implicits for LongEnum
 */
trait PlayJsonLongValeEnum[EntryType <: LongEnumEntry] extends PlayJsonValueEnum[Long, EntryType] { this: LongEnum[EntryType] =>
  implicit val format: Format[EntryType] = EnumFormats.formats(this)
}

/**
 * Json format implicits for ShortEnum
 */
trait PlayJsonShortValeEnum[EntryType <: ShortEnumEntry] extends PlayJsonValueEnum[Short, EntryType] { this: ShortEnum[EntryType] =>
  implicit val format: Format[EntryType] = EnumFormats.formats(this)
}