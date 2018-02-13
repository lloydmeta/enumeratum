package enumeratum.values

import io.getquill.MappedEncoding

sealed trait QuillValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType], QuillType] {
  this: ValueEnum[ValueType, EntryType] =>

  /**
    * Implicit Encoder for this enum
    */
  implicit val quillEncoder: MappedEncoding[EntryType, QuillType]

  /**
    * Implicit Decoder for this enum
    */
  implicit val quillDecoder: MappedEncoding[QuillType, EntryType]
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
trait IntQuillEnum[EntryType <: IntEnumEntry] extends QuillValueEnum[Int, EntryType, Int] {
  this: ValueEnum[Int, EntryType] =>
  implicit val quillEncoder: MappedEncoding[EntryType, Int] = Quill.encoder(this)
  implicit val quillDecoder: MappedEncoding[Int, EntryType] = Quill.decoder(this)
}

/**
  * QuillEnum for LongEnumEntry
  */
trait LongQuillEnum[EntryType <: LongEnumEntry] extends QuillValueEnum[Long, EntryType, Long] {
  this: ValueEnum[Long, EntryType] =>
  implicit val quillEncoder: MappedEncoding[EntryType, Long] = Quill.encoder(this)
  implicit val quillDecoder: MappedEncoding[Long, EntryType] = Quill.decoder(this)
}

/**
  * QuillEnum for ShortEnumEntry
  */
trait ShortQuillEnum[EntryType <: ShortEnumEntry] extends QuillValueEnum[Short, EntryType, Short] {
  this: ValueEnum[Short, EntryType] =>
  implicit val quillEncoder: MappedEncoding[EntryType, Short] = Quill.encoder(this)
  implicit val quillDecoder: MappedEncoding[Short, EntryType] = Quill.decoder(this)
}

/**
  * QuillEnum for StringEnumEntry
  */
trait StringQuillEnum[EntryType <: StringEnumEntry] extends QuillValueEnum[String, EntryType, String] {
  this: ValueEnum[String, EntryType] =>
  implicit val quillEncoder: MappedEncoding[EntryType, String] = Quill.encoder(this)
  implicit val quillDecoder: MappedEncoding[String, EntryType] = Quill.decoder(this)
}

/**
  * QuillEnum for CharEnumEntry
  */
trait CharQuillEnum[EntryType <: CharEnumEntry] extends QuillValueEnum[Char, EntryType, String] {
  this: ValueEnum[Char, EntryType] =>

  /**
    * Because all existing Quill contexts do not have built-in Encoders for Char, convert it to a String instead.
    */
  implicit val quillEncoder: MappedEncoding[EntryType, String] = MappedEncoding(enum => String.valueOf(enum.value))

  /**
    * Because all existing Quill contexts do not have built-in Decoders for Char, convert it from a String instead.
    */
  implicit val quillDecoder: MappedEncoding[String, EntryType] = MappedEncoding(str => withValue(str.charAt(0)))
}

/**
  * QuillEnum for ByteEnumEntry
  */
trait ByteQuillEnum[EntryType <: ByteEnumEntry] extends QuillValueEnum[Byte, EntryType, Byte] {
  this: ValueEnum[Byte, EntryType] =>
  implicit val quillEncoder: MappedEncoding[EntryType, Byte] = Quill.encoder(this)
  implicit val quillDecoder: MappedEncoding[Byte, EntryType] = Quill.decoder(this)
}
