package enumeratum.values

import doobie.util._
import doobie.Meta

object Doobie {

  def meta[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]
  )(
      implicit
      get: Get[ValueType],
      put: Put[ValueType]
  ): Meta[EntryType] =
    new Meta[ValueType](get, put).imap(enum.withValue)(_.value)
}
