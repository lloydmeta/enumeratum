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
  implicit lazy val queryBindable: QueryStringBindable[EntryType] = new QueryStringBindable[EntryType] {
    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, EntryType]] = {
      baseQueryBindable.bind(key, params).map(_.right.flatMap { s =>
        val maybeBound = enum.withValueOpt(s)
        maybeBound match {
          case Some(obj) => Right(obj)
          case None => Left(s"Unknown value supplied for $enum '$s'")
        }
      })
    }

    def unbind(key: String, entry: EntryType): String = s"$key=${entry.value}"
  }
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