package enumeratum.values

import io.circe.{Codec, Decoder, Encoder, KeyDecoder, KeyEncoder}

/** Created by Lloyd on 4/14/16.
  *
  * Copyright 2016
  */
sealed trait CirceValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {
  this: ValueEnum[ValueType, EntryType] =>

  /** Implicit Encoder for this enum
    */
  implicit def circeEncoder: Encoder[EntryType]

  /** Implicit Decoder for this enum
    */
  implicit def circeDecoder: Decoder[EntryType]

  /** Implicit Codec for this enum
    */
  implicit def circeCodec: Codec[EntryType] = Codec.from(circeDecoder, circeEncoder)
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
  * scala> case object ShirtSize extends IntEnum[ShirtSize] with IntCirceEnum[ShirtSize] {
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
trait IntCirceEnum[EntryType <: IntEnumEntry] extends CirceValueEnum[Int, EntryType] {
  this: ValueEnum[Int, EntryType] =>
  implicit val circeEncoder: Encoder[EntryType] = Circe.encoder(this)
  implicit val circeDecoder: Decoder[EntryType] = Circe.decoder(this)
}

/** CirceEnum for LongEnumEntry
  */
trait LongCirceEnum[EntryType <: LongEnumEntry] extends CirceValueEnum[Long, EntryType] {
  this: ValueEnum[Long, EntryType] =>
  implicit val circeEncoder: Encoder[EntryType] = Circe.encoder(this)
  implicit val circeDecoder: Decoder[EntryType] = Circe.decoder(this)
}

/** CirceEnum for ShortEnumEntry
  */
trait ShortCirceEnum[EntryType <: ShortEnumEntry] extends CirceValueEnum[Short, EntryType] {
  this: ValueEnum[Short, EntryType] =>
  implicit val circeEncoder: Encoder[EntryType] = Circe.encoder(this)
  implicit val circeDecoder: Decoder[EntryType] = Circe.decoder(this)
}

/** CirceEnum for StringEnumEntry
  */
trait StringCirceEnum[EntryType <: StringEnumEntry] extends CirceValueEnum[String, EntryType] {
  this: ValueEnum[String, EntryType] =>
  implicit val circeEncoder: Encoder[EntryType] = Circe.encoder(this)
  implicit val circeDecoder: Decoder[EntryType] = Circe.decoder(this)

  implicit val circeKeyEncoder: KeyEncoder[EntryType] = Circe.keyEncoder(this)
  implicit val circeKeyDecoder: KeyDecoder[EntryType] = Circe.keyDecoder(this)
}

/** CirceEnum for CharEnumEntry
  */
trait CharCirceEnum[EntryType <: CharEnumEntry] extends CirceValueEnum[Char, EntryType] {
  this: ValueEnum[Char, EntryType] =>
  implicit val circeEncoder: Encoder[EntryType] = Circe.encoder(this)
  implicit val circeDecoder: Decoder[EntryType] = Circe.decoder(this)
}

/** CirceEnum for ByteEnumEntry
  */
trait ByteCirceEnum[EntryType <: ByteEnumEntry] extends CirceValueEnum[Byte, EntryType] {
  this: ValueEnum[Byte, EntryType] =>
  implicit val circeEncoder: Encoder[EntryType] = Circe.encoder(this)
  implicit val circeDecoder: Decoder[EntryType] = Circe.decoder(this)
}
