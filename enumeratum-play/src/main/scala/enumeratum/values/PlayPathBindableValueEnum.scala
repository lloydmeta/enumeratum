package enumeratum.values

import play.api.mvc.PathBindable
import play.api.routing.sird.PathBindableExtractor

/** Created by Lloyd on 4/13/16.
  *
  * Copyright 2016
  */
sealed trait PlayPathBindableValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {
  _enum: ValueEnum[ValueType, EntryType] =>

  /** Implicit path binder for Play's default router
    */
  implicit def pathBindable: PathBindable[EntryType]

  /** Binder for [[play.api.routing.sird]] router
    *
    * Example:
    *
    * {{{
    * scala> import play.api.routing.sird._
    * scala> import play.api.routing._
    * scala> import play.api.mvc._
    *
    * scala> sealed abstract class Greeting(val value: Int) extends IntEnumEntry
    *
    * scala> object Greeting extends IntPlayEnum[Greeting] {
    *     |   val values = findValues
    *     |   case object Hello   extends Greeting(1)
    *     |   case object GoodBye extends Greeting(2)
    *     |   case object Hi      extends Greeting(3)
    *     |   case object Bye     extends Greeting(4)
    *     | }
    *
    * scala> val router = Router.from {
    *     |   case GET(p"/hello/${Greeting.fromPath(greeting)}") => Action {
    *     |     Results.Ok(s"$greeting")
    *     |   }
    *     | }
    * scala> router.routes
    * res0: Router.Routes = <function1>
    * }}}
    */
  lazy val fromPath = new PathBindableExtractor[EntryType]
}

/** Path Bindable implicits for IntEnum
  */
trait IntPlayPathBindableValueEnum[EntryType <: IntEnumEntry]
    extends PlayPathBindableValueEnum[Int, EntryType] { self: IntEnum[EntryType] =>
  implicit val pathBindable: PathBindable[EntryType] =
    UrlBinders.pathBinder(self)
}

/** Path Bindable implicits for LongEnum
  */
trait LongPlayPathBindableValueEnum[EntryType <: LongEnumEntry]
    extends PlayPathBindableValueEnum[Long, EntryType] { self: LongEnum[EntryType] =>
  implicit val pathBindable: PathBindable[EntryType] =
    UrlBinders.pathBinder(self)
}

/** Path Bindable implicits for ShortEnum
  */
trait ShortPlayPathBindableValueEnum[EntryType <: ShortEnumEntry]
    extends PlayPathBindableValueEnum[Short, EntryType] { self: ShortEnum[EntryType] =>
  implicit val pathBindable: PathBindable[EntryType] =
    UrlBinders.pathBinder(self)(PathBindable.bindableInt.transform(_.toShort, _.toInt))
}

/** Path Bindable implicits for StringEnum
  */
trait StringPlayPathBindableValueEnum[EntryType <: StringEnumEntry]
    extends PlayPathBindableValueEnum[String, EntryType] { self: StringEnum[EntryType] =>
  implicit val pathBindable: PathBindable[EntryType] =
    UrlBinders.pathBinder(self)(PathBindable.bindableString)
}

/** Path Bindable implicits for CharEnum
  */
trait CharPlayPathBindableValueEnum[EntryType <: CharEnumEntry]
    extends PlayPathBindableValueEnum[Char, EntryType] { self: CharEnum[EntryType] =>
  implicit val pathBindable: PathBindable[EntryType] =
    UrlBinders.pathBinder(self)(PathBindable.bindableChar)
}

/** Path Bindable implicits for ByteEnum
  */
trait BytePlayPathBindableValueEnum[EntryType <: ByteEnumEntry]
    extends PlayPathBindableValueEnum[Byte, EntryType] { self: ByteEnum[EntryType] =>
  implicit val pathBindable: PathBindable[EntryType] =
    UrlBinders.pathBinder(self)(PathBindable.bindableInt.transform(_.toByte, _.toInt))
}
