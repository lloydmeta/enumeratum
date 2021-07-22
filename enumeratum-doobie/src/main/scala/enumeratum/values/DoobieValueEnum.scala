package enumeratum.values

import doobie.util._
import doobie.Meta

sealed trait DoobieValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType], DoobieType] {
  this: ValueEnum[ValueType, EntryType] =>

  implicit val meta: Meta[EntryType]
}

/** DoobieEnum for IntEnumEntry
  *
  * {{{
  * scala> import enumeratum.values._
  * scala> import doobie._
  * scala> import doobie.implicits._
  *
  * scala> sealed abstract class ShirtSize(val value:Int) extends IntEnumEntry
  * scala> case object ShirtSize extends IntEnum[ShirtSize] with IntDoobieEnum[ShirtSize] {
  *     |  case object Small  extends ShirtSize(1)
  *     |  case object Medium extends ShirtSize(2)
  *     |  case object Large  extends ShirtSize(3)
  *     |  val values = findValues
  *     | }
  *
  * scala> case class Shirt(size: ShirtSize)
  *
  * scala> sql"select size from Shirt".query[Shirt].to[List]
  * }}}
  */
trait IntDoobieEnum[EntryType <: IntEnumEntry] extends DoobieValueEnum[Int, EntryType, Int] {
  this: ValueEnum[Int, EntryType] =>
  implicit val meta: Meta[EntryType] = Doobie.meta(this)
}

/** DoobieEnum for LongEnumEntry
  */
trait LongDoobieEnum[EntryType <: LongEnumEntry] extends DoobieValueEnum[Long, EntryType, Long] {
  this: ValueEnum[Long, EntryType] =>
  implicit val meta: Meta[EntryType] = Doobie.meta(this)
}

/** DoobieEnum for ShortEnumEntry
  */
trait ShortDoobieEnum[EntryType <: ShortEnumEntry]
    extends DoobieValueEnum[Short, EntryType, Short] {
  this: ValueEnum[Short, EntryType] =>
  implicit val meta: Meta[EntryType] = Doobie.meta(this)
}

/** DoobieEnum for StringEnumEntry
  */
trait StringDoobieEnum[EntryType <: StringEnumEntry]
    extends DoobieValueEnum[String, EntryType, String] {
  this: ValueEnum[String, EntryType] =>
  implicit val meta: Meta[EntryType] = Doobie.meta(this)
}

/** DoobieEnum for CharEnumEntry
  */
trait CharDoobieEnum[EntryType <: CharEnumEntry] extends DoobieValueEnum[Char, EntryType, String] {
  this: ValueEnum[Char, EntryType] =>

  implicit val meta: Meta[EntryType] =
    Meta[String].imap(str => withValue(str.charAt(0)))(enum => String.valueOf(enum.value))
}

/** DoobieEnum for ByteEnumEntry
  */
trait ByteDoobieEnum[EntryType <: ByteEnumEntry] extends DoobieValueEnum[Byte, EntryType, Byte] {
  this: ValueEnum[Byte, EntryType] =>
  implicit val meta: Meta[EntryType] = Doobie.meta(this)
}
