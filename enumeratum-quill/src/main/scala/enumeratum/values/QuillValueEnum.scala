package enumeratum.values

import io.getquill.MappedEncoding

sealed trait QuillValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {
  this: ValueEnum[ValueType, EntryType] =>

  /**
    * Implicit Encoder for this enum
    */
  implicit val quillEncoder: MappedEncoding[EntryType, ValueType] = Quill.encoder(this)

  /**
    * Implicit Decoder for this enum
    */
  implicit val quillDecoder: MappedEncoding[ValueType, EntryType] = Quill.decoder(this)
}

/**
  * QuillEnum for IntEnumEntry
  *
  * {{{
  * scala> import enumeratum.values._
  * scala> import io.getquill._
  *
  * scala> sealed abstract class ShirtSize(val value:Int) extends IntEnumEntry
  * scala> case object ShirtSize extends IntEnum[ShirtSize] with IntQuillEnum[ShirtSize] {
  *      |  case object Small  extends ShirtSize(1)
  *      |  case object Medium extends ShirtSize(2)
  *      |  case object Large  extends ShirtSize(3)
  *      |  val values = findValues
  *      | }
  *
  * scala> case class Shirt(size: ShirtSize)
  *
  * scala> val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
  * scala> import ctx._
  *
  * scala> val size: ShirtSize = ShirtSize.Small
  *
  * scala> ctx.run(query[Shirt].insert(_.size -> lift(size))).string
  * res0: String = INSERT INTO Shirt (size) VALUES (?)
  * }}}
  */
trait IntQuillEnum[EntryType <: IntEnumEntry] extends QuillValueEnum[Int, EntryType] {
  this: ValueEnum[Int, EntryType] =>
}

/**
  * QuillEnum for LongEnumEntry
  */
trait LongQuillEnum[EntryType <: LongEnumEntry] extends QuillValueEnum[Long, EntryType] {
  this: ValueEnum[Long, EntryType] =>
}

/**
  * QuillEnum for ShortEnumEntry
  */
trait ShortQuillEnum[EntryType <: ShortEnumEntry] extends QuillValueEnum[Short, EntryType] {
  this: ValueEnum[Short, EntryType] =>
}

/**
  * QuillEnum for StringEnumEntry
  */
trait StringQuillEnum[EntryType <: StringEnumEntry] extends QuillValueEnum[String, EntryType] {
  this: ValueEnum[String, EntryType] =>
}

/**
  * QuillEnum for CharEnumEntry
  */
trait CharQuillEnum[EntryType <: CharEnumEntry] extends QuillValueEnum[Char, EntryType] {
  this: ValueEnum[Char, EntryType] =>
}

/**
  * QuillEnum for ByteEnumEntry
  */
trait ByteQuillEnum[EntryType <: ByteEnumEntry] extends QuillValueEnum[Byte, EntryType] {
  this: ValueEnum[Byte, EntryType] =>
}
