package enumeratum.values

import io.circe.{Codec, Decoder, Encoder, KeyDecoder, KeyEncoder}

/** Created by Lloyd on 4/14/16.
  *
  * Copyright 2016
  */
sealed trait CirceCodecValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {
  this: ValueEnum[ValueType, EntryType] =>

  /** Implicit Codec for this enum
    */
  implicit def circeCodec: Codec[EntryType]
}

/** CirceEnum for IntEnumEntry
  *
  * {{{
  * scala> import enumeratum.values._
  * scala> import cats.syntax.either._
  * scala> import io.circe._
  * scala> import io.circe.syntax._
  *
  * scala> sealed abstract class ShirtSize(val value:Int) extends IntEnumEntry
  * scala> case object ShirtSize extends IntEnum[ShirtSize] with IntCirceCodecEnum[ShirtSize] {
  *     |  case object Small  extends ShirtSize(1)
  *     |  case object Medium extends ShirtSize(2)
  *     |  case object Large  extends ShirtSize(3)
  *     |  val values = findValues
  *     | }
  *
  * scala> val size: ShirtSize = ShirtSize.Small
  *
  * scala> size.asJson
  * res0: Json = 1
  *
  * scala> Json.fromInt(3).as[ShirtSize]
  * res1: Decoder.Result[ShirtSize] = Right(Large)
  *
  * scala> Json.fromInt(10).as[ShirtSize]
  * res2: Decoder.Result[ShirtSize] = Left(DecodingFailure(10 is not a member of enum ShirtSize, List()))
  * }}}
  */
trait IntCirceCodecEnum[EntryType <: IntEnumEntry] extends CirceCodecValueEnum[Int, EntryType] {
  this: ValueEnum[Int, EntryType] =>
  implicit val circeCodec: Codec[EntryType] = Circe.codec(this)
}

/** CirceEnum for LongEnumEntry
  */
trait LongCirceCodecEnum[EntryType <: LongEnumEntry] extends CirceCodecValueEnum[Long, EntryType] {
  this: ValueEnum[Long, EntryType] =>
  implicit val circeCodec: Codec[EntryType] = Circe.codec(this)
}

/** CirceEnum for ShortEnumEntry
  */
trait ShortCirceCodecEnum[EntryType <: ShortEnumEntry]
    extends CirceCodecValueEnum[Short, EntryType] {
  this: ValueEnum[Short, EntryType] =>
  implicit val circeCodec: Codec[EntryType] = Circe.codec(this)
}

/** CirceEnum for StringEnumEntry
  */
trait StringCirceCodecEnum[EntryType <: StringEnumEntry]
    extends CirceCodecValueEnum[String, EntryType] {
  this: ValueEnum[String, EntryType] =>
  implicit val circeCodec: Codec[EntryType] = Circe.codec(this)
}

/** CirceEnum for CharEnumEntry
  */
trait CharCirceCodecEnum[EntryType <: CharEnumEntry] extends CirceCodecValueEnum[Char, EntryType] {
  this: ValueEnum[Char, EntryType] =>
  implicit val circeCodec: Codec[EntryType] = Circe.codec(this)
}

/** CirceEnum for ByteEnumEntry
  */
trait ByteCirceCodecEnum[EntryType <: ByteEnumEntry] extends CirceCodecValueEnum[Byte, EntryType] {
  this: ValueEnum[Byte, EntryType] =>
  implicit val circeCodec: Codec[EntryType] = Circe.codec(this)
}
