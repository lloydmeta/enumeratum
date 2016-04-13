package enumeratum.values

import play.api.mvc.QueryStringBindable
import play.api.routing.sird.PathBindableExtractor

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */

sealed trait PlayQueryBindableValueEnum[ValueType <: AnyVal, EntryType <: ValueEnumEntry[ValueType], EnumType <: ValueEnum[EntryType, ValueType]] { enum: EnumType =>

  protected def baseQueryBindable: QueryStringBindable[ValueType]

  /**
   * Implicit path binder for Play's default router
   */
  implicit lazy val queryBindable: QueryStringBindable[EntryType] = UrlBinders.queryBinder(baseQueryBindable)(enum)
}

/**
 * Query Bindable implicits for IntEnum
 */
trait PlayQueryBindableIntValueEnum[EntryType <: IntEnumEntry] extends PlayQueryBindableValueEnum[Int, EntryType, IntEnum[EntryType]] { this: IntEnum[EntryType] =>
  protected val baseQueryBindable: QueryStringBindable[Int] = QueryStringBindable.bindableInt
}

/**
 * Query Bindable implicits for LongEnum
 */
trait PlayQueryBindableLongValueEnum[EntryType <: LongEnumEntry] extends PlayQueryBindableValueEnum[Long, EntryType, LongEnum[EntryType]] { this: LongEnum[EntryType] =>
  protected val baseQueryBindable: QueryStringBindable[Long] = QueryStringBindable.bindableLong
}

/**
 * Query Bindable implicits for ShortEnum
 */
trait PlayQueryBindableShortValueEnum[EntryType <: ShortEnumEntry] extends PlayQueryBindableValueEnum[Short, EntryType, ShortEnum[EntryType]] { this: ShortEnum[EntryType] =>
  protected val baseQueryBindable: QueryStringBindable[Short] = QueryStringBindable.bindableInt.transform(_.toShort, _.toInt)
}