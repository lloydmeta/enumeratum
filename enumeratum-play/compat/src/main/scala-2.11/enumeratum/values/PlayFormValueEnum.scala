package enumeratum.values

import play.api.data.format.{ Formatter, Formats }
import play.api.data.{ FormError, Forms => PlayForms, Mapping }

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */

sealed trait PlayFormValueEnum[ValueType <: AnyVal, EntryType <: ValueEnumEntry[ValueType], EnumType <: ValueEnum[EntryType, ValueType]] { enum: EnumType =>

  protected def baseFormatter: Formatter[ValueType]

  private lazy val formatter: Formatter[EntryType] = new Formatter[EntryType] {
    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], EntryType] = baseFormatter.bind(key, data).right.flatMap { s =>
      val maybeBound = enum.withValueOpt(s)
      maybeBound match {
        case Some(obj) => Right(obj)
        case None => Left(Seq(FormError(key, "error.enum", Nil)))
      }
    }

    def unbind(key: String, value: EntryType): Map[String, String] = Map(key -> value.value.toString)
  }

  /**
   * Field for mapping this enum in Forms
   */
  lazy val formField: Mapping[EntryType] = PlayForms.of(formatter)

}

/**
 * Form Bindable implicits for IntEnum
 */
trait PlayFormIntValueEnum[EntryType <: IntEnumEntry] extends PlayFormValueEnum[Int, EntryType, IntEnum[EntryType]] { this: IntEnum[EntryType] =>
  protected val baseFormatter: Formatter[Int] = Formats.intFormat
}

/**
 * Form Bindable implicits for LongEnum
 */
trait PlayFormLongValueEnum[EntryType <: LongEnumEntry] extends PlayFormValueEnum[Long, EntryType, LongEnum[EntryType]] { this: LongEnum[EntryType] =>
  protected val baseFormatter: Formatter[Long] = Formats.longFormat
}

/**
 * Form Bindable implicits for ShortEnum
 */
trait PlayFormShortValueEnum[EntryType <: ShortEnumEntry] extends PlayFormValueEnum[Short, EntryType, ShortEnum[EntryType]] { this: ShortEnum[EntryType] =>
  protected val baseFormatter: Formatter[Short] = Formats.shortFormat
}