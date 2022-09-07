package enumeratum.values

import play.api.mvc.QueryStringBindable

/** Created by Lloyd on 4/13/16.
  *
  * Copyright 2016
  */
sealed trait PlayQueryBindableValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {
  _enum: ValueEnum[ValueType, EntryType] =>

  /** Implicit path binder for Play's default router
    */
  implicit def queryBindable: QueryStringBindable[EntryType]
}

/** Query Bindable implicits for IntEnum
  */
trait IntPlayQueryBindableValueEnum[EntryType <: IntEnumEntry]
    extends PlayQueryBindableValueEnum[Int, EntryType] { self: IntEnum[EntryType] =>
  implicit val queryBindable: QueryStringBindable[EntryType] =
    UrlBinders.queryBinder(self)
}

/** Query Bindable implicits for LongEnum
  */
trait LongPlayQueryBindableValueEnum[EntryType <: LongEnumEntry]
    extends PlayQueryBindableValueEnum[Long, EntryType] { self: LongEnum[EntryType] =>
  implicit val queryBindable: QueryStringBindable[EntryType] =
    UrlBinders.queryBinder(self)
}

/** Query Bindable implicits for ShortEnum
  */
trait ShortPlayQueryBindableValueEnum[EntryType <: ShortEnumEntry]
    extends PlayQueryBindableValueEnum[Short, EntryType] { self: ShortEnum[EntryType] =>
  implicit val queryBindable: QueryStringBindable[EntryType] =
    UrlBinders.queryBinder(self)(QueryStringBindable.bindableInt.transform(_.toShort, _.toInt))
}

/** Query Bindable implicits for StringEnum
  */
trait StringPlayQueryBindableValueEnum[EntryType <: StringEnumEntry]
    extends PlayQueryBindableValueEnum[String, EntryType] { self: StringEnum[EntryType] =>
  implicit val queryBindable: QueryStringBindable[EntryType] =
    UrlBinders.queryBinder(self)(QueryStringBindable.bindableString)
}

/** Query Bindable implicits for CharEnum
  */
trait CharPlayQueryBindableValueEnum[EntryType <: CharEnumEntry]
    extends PlayQueryBindableValueEnum[Char, EntryType] { self: CharEnum[EntryType] =>
  implicit val queryBindable: QueryStringBindable[EntryType] =
    UrlBinders.queryBinder(self)(QueryStringBindable.bindableChar)
}

/** Query Bindable implicits for ByteEnum
  */
trait BytePlayQueryBindableValueEnum[EntryType <: ByteEnumEntry]
    extends PlayQueryBindableValueEnum[Byte, EntryType] { self: ByteEnum[EntryType] =>
  implicit val queryBindable: QueryStringBindable[EntryType] =
    UrlBinders.queryBinder(self)(QueryStringBindable.bindableInt.transform(_.toByte, _.toInt))
}
