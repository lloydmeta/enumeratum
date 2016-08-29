package enumeratum.values

import play.api.mvc.QueryStringBindable
import play.api.routing.sird.PathBindableExtractor

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */

sealed trait PlayQueryBindableValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] { enum: ValueEnum[ValueType, EntryType] =>

  /**
   * Implicit path binder for Play's default router
   */
  implicit def queryBindable: QueryStringBindable[EntryType]
}

/**
 * Query Bindable implicits for IntEnum
 */
trait IntPlayQueryBindableValueEnum[EntryType <: IntEnumEntry] extends PlayQueryBindableValueEnum[Int, EntryType] { this: IntEnum[EntryType] =>
  implicit val queryBindable: QueryStringBindable[EntryType] = UrlBinders.queryBinder(this)
}

/**
 * Query Bindable implicits for LongEnum
 */
trait LongPlayQueryBindableValueEnum[EntryType <: LongEnumEntry] extends PlayQueryBindableValueEnum[Long, EntryType] { this: LongEnum[EntryType] =>
  implicit val queryBindable: QueryStringBindable[EntryType] = UrlBinders.queryBinder(this)
}

/**
 * Query Bindable implicits for ShortEnum
 */
trait ShortPlayQueryBindableValueEnum[EntryType <: ShortEnumEntry] extends PlayQueryBindableValueEnum[Short, EntryType] { this: ShortEnum[EntryType] =>
  implicit val queryBindable: QueryStringBindable[EntryType] = UrlBinders.queryBinder(this)(QueryStringBindable.bindableInt.transform(_.toShort, _.toInt))
}

/**
 * Query Bindable implicits for StringEnum
 */
trait StringPlayQueryBindableValueEnum[EntryType <: StringEnumEntry] extends PlayQueryBindableValueEnum[String, EntryType] { this: StringEnum[EntryType] =>
  implicit val queryBindable: QueryStringBindable[EntryType] = UrlBinders.queryBinder(this)(QueryStringBindable.bindableString)
}