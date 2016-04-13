package enumeratum.values

import play.api.data.format.{ Formatter, Formats }
import play.api.data.Mapping

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */

sealed trait PlayFormValueEnum[ValueType <: AnyVal, EntryType <: ValueEnumEntry[ValueType]] { enum: ValueEnum[ValueType, EntryType] =>

  protected def baseFormatter: Formatter[ValueType]

  /**
   * Field for mapping this enum in Forms
   */
  lazy val formField: Mapping[EntryType] = Forms.enum(baseFormatter)(enum)

}

/**
 * Form Bindable implicits for IntEnum
 */
trait PlayFormIntValueEnum[EntryType <: IntEnumEntry] extends PlayFormValueEnum[Int, EntryType] { this: IntEnum[EntryType] =>
  protected val baseFormatter: Formatter[Int] = Formats.intFormat
}

/**
 * Form Bindable implicits for LongEnum
 */
trait PlayFormLongValueEnum[EntryType <: LongEnumEntry] extends PlayFormValueEnum[Long, EntryType] { this: LongEnum[EntryType] =>
  protected val baseFormatter: Formatter[Long] = Formats.longFormat
}

/**
 * Form Bindable implicits for ShortEnum
 */
trait PlayFormShortValueEnum[EntryType <: ShortEnumEntry] extends PlayFormValueEnum[Short, EntryType] { this: ShortEnum[EntryType] =>
  protected val baseFormatter: Formatter[Short] = Formats.shortFormat
}