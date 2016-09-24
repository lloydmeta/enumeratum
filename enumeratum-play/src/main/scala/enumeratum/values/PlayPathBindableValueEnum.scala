package enumeratum.values

import play.api.mvc.PathBindable
import play.api.routing.sird.PathBindableExtractor

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */
sealed trait PlayPathBindableValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] { enum: ValueEnum[ValueType, EntryType] =>

  /**
   * Implicit path binder for Play's default router
   */
  implicit def pathBindable: PathBindable[EntryType]

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
trait IntPlayPathBindableValueEnum[EntryType <: IntEnumEntry] extends PlayPathBindableValueEnum[Int, EntryType] { this: IntEnum[EntryType] =>
  implicit val pathBindable: PathBindable[EntryType] = UrlBinders.pathBinder(this)
}

/**
 * Path Bindable implicits for LongEnum
 */
trait LongPlayPathBindableValueEnum[EntryType <: LongEnumEntry] extends PlayPathBindableValueEnum[Long, EntryType] { this: LongEnum[EntryType] =>
  implicit val pathBindable: PathBindable[EntryType] = UrlBinders.pathBinder(this)
}

/**
 * Path Bindable implicits for ShortEnum
 */
trait ShortPlayPathBindableValueEnum[EntryType <: ShortEnumEntry] extends PlayPathBindableValueEnum[Short, EntryType] { this: ShortEnum[EntryType] =>
  implicit val pathBindable: PathBindable[EntryType] = UrlBinders.pathBinder(this)(PathBindable.bindableInt.transform(_.toShort, _.toInt))
}

/**
 * Path Bindable implicits for StringEnum
 */
trait StringPlayPathBindableValueEnum[EntryType <: StringEnumEntry] extends PlayPathBindableValueEnum[String, EntryType] { this: StringEnum[EntryType] =>
  implicit val pathBindable: PathBindable[EntryType] = UrlBinders.pathBinder(this)(PathBindable.bindableString)
}

/**
 * Path Bindable implicits for CharEnum
 */
trait CharPlayPathBindableValueEnum[EntryType <: CharEnumEntry] extends PlayPathBindableValueEnum[Char, EntryType] { this: CharEnum[EntryType] =>
  implicit val pathBindable: PathBindable[EntryType] = UrlBinders.pathBinder(this)(PathBindable.bindableChar)
}

/**
 * Path Bindable implicits for ByteEnum
 */
trait BytePlayPathBindableValueEnum[EntryType <: ByteEnumEntry] extends PlayPathBindableValueEnum[Byte, EntryType] { this: ByteEnum[EntryType] =>
  implicit val pathBindable: PathBindable[EntryType] = UrlBinders.pathBinder(this)(PathBindable.bindableInt.transform(_.toByte, _.toInt))
}