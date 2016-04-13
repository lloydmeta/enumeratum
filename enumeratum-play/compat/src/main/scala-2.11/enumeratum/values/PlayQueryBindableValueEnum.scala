package enumeratum.values

import play.api.mvc.QueryStringBindable
import play.api.routing.sird.PathBindableExtractor

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */

sealed trait PlayQueryBindableValueEnum[ValueType <: AnyVal, EntryType <: ValueEnumEntry[ValueType]] { enum: ValueEnum[EntryType, ValueType] =>

  /**
   * Implicit path binder for Play's default router
   */
  implicit def queryBindable: QueryStringBindable[EntryType]
}

/**
 * Query Bindable implicits for IntEnum
 */
trait PlayQueryBindableIntValueEnum[EntryType <: IntEnumEntry] extends PlayQueryBindableValueEnum[Int, EntryType] { this: IntEnum[EntryType] =>
  implicit val queryBindable: QueryStringBindable[EntryType] = UrlBinders.queryBinder(this)
}

/**
 * Query Bindable implicits for LongEnum
 */
trait PlayQueryBindableLongValueEnum[EntryType <: LongEnumEntry] extends PlayQueryBindableValueEnum[Long, EntryType] { this: LongEnum[EntryType] =>
  implicit val queryBindable: QueryStringBindable[EntryType] = UrlBinders.queryBinder(this)
}

/**
 * Query Bindable implicits for ShortEnum
 */
trait PlayQueryBindableShortValueEnum[EntryType <: ShortEnumEntry] extends PlayQueryBindableValueEnum[Short, EntryType] { this: ShortEnum[EntryType] =>
  implicit val queryBindable: QueryStringBindable[EntryType] = UrlBinders.queryBinder(this)(QueryStringBindable.bindableInt.transform(_.toShort, _.toInt))
}