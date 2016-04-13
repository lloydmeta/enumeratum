package enumeratum.values

import upickle.default.Aliases.RW
import upickle.default.ReadWriter
import UPickler._

/**
 * Created by Lloyd on 4/14/16.
 *
 * Copyright 2016
 */

sealed trait UPickleValueEnum[ValueType <: AnyVal, EntryType <: ValueEnumEntry[ValueType]] { this: ValueEnum[ValueType, EntryType] =>

  /**
   * UPickle ReadWriter
   */
  implicit def uPickleReadWriter: RW[EntryType]

}

trait IntUPickleEnum[EntryType <: IntEnumEntry] extends UPickleValueEnum[Int, EntryType] { this: ValueEnum[Int, EntryType] =>
  implicit val uPickleReadWriter: RW[EntryType] = ReadWriter(writer(this).write, reader(enum = this).read)
}

trait LongUPickleEnum[EntryType <: LongEnumEntry] extends UPickleValueEnum[Long, EntryType] { this: ValueEnum[Long, EntryType] =>
  implicit val uPickleReadWriter: RW[EntryType] = ReadWriter(writer(this).write, reader(enum = this).read)
}

trait ShortUPickleEnum[EntryType <: ShortEnumEntry] extends UPickleValueEnum[Short, EntryType] { this: ValueEnum[Short, EntryType] =>
  implicit val uPickleReadWriter: RW[EntryType] = ReadWriter(writer(this).write, reader(enum = this).read)
}
