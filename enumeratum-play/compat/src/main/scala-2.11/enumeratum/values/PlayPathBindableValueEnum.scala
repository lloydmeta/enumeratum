package enumeratum.values

import play.api.mvc.PathBindable
import play.api.routing.sird.PathBindableExtractor

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */
sealed trait PlayPathBindableValueEnum[ValueType <: AnyVal, EntryType <: ValueEnumEntry[ValueType], EnumType <: ValueEnum[EntryType, ValueType]] { enum: EnumType =>

  protected def basePathBindable: PathBindable[ValueType]

  /**
   * Implicit path binder for Play's default router
   */
  implicit lazy val pathBindable: PathBindable[EntryType] = new PathBindable[EntryType] {
    def bind(key: String, value: String): Either[String, EntryType] = basePathBindable.bind(key, value).right.flatMap { b =>
      val maybBound = enum.withValueOpt(b)
      maybBound match {
        case Some(obj) => Right(obj)
        case None => Left(s"Unknown value supplied for $enum '" + value + "'")
      }
    }

    def unbind(key: String, value: EntryType): String = value.value.toString
  }

  /**
   * Binder for [[play.api.routing.sird]] router
   *
   * Example:
   *
   * {{{
   *  import play.api.routing.sird._
   *  import play.api.routing._
   *  import play.api.mvc._
   *
   *  Router.from {
   *    case GET(p"/hello/${Greeting.fromPath(greeting)}") => Action {
   *      Results.Ok(s"$greeting")
   *    }
   *  }
   * }}}
   */
  lazy val fromPath = new PathBindableExtractor[EntryType]
}

/**
 * Path Bindable implicits for IntEnum
 */
trait PlayPathBindableIntValueEnum[EntryType <: IntEnumEntry] extends PlayPathBindableValueEnum[Int, EntryType, IntEnum[EntryType]] { this: IntEnum[EntryType] =>
  protected val basePathBindable: PathBindable[Int] = PathBindable.bindableInt
}

/**
 * Path Bindable implicits for LongEnum
 */
trait PlayPathBindableLongValueEnum[EntryType <: LongEnumEntry] extends PlayPathBindableValueEnum[Long, EntryType, LongEnum[EntryType]] { this: LongEnum[EntryType] =>
  protected val basePathBindable: PathBindable[Long] = PathBindable.bindableLong
}

/**
 * Path Bindable implicits for ShortEnum
 */
trait PlayPathBindableShortValueEnum[EntryType <: ShortEnumEntry] extends PlayPathBindableValueEnum[Short, EntryType, ShortEnum[EntryType]] { this: ShortEnum[EntryType] =>
  protected val basePathBindable: PathBindable[Short] = PathBindable.bindableInt.transform(_.toShort, _.toInt)
}