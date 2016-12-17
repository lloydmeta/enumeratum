package enumeratum.values

import argonaut._
import Argonaut._

/**
  * Created by alonsodomin on 14/10/2016.
  */
/**
  * Base trait for Value-based Enumeratum Enums that automatically adds Argonaut Json
  * Encoders and Decoders.
  *
  * Example
  *
  *
  * {{{
  *  scala> import enumeratum.values._
  *  scala> import argonaut._
  *  scala> import Argonaut._
  *
  *  scala> sealed abstract class TrafficLight(val value: Int) extends IntEnumEntry
  *  scala> case object TrafficLight
  *       |     extends IntEnum[TrafficLight]
  *       |       with IntArgonautEnum[TrafficLight] {
  *       |   case object Red    extends TrafficLight(1)
  *       |   case object Yellow extends TrafficLight(2)
  *       |   case object Green  extends TrafficLight(3)
  *       |   val values = findValues
  *       | }
  *
  *  scala> val light: TrafficLight = TrafficLight.Red
  *  scala> light.asJson
  *  res0: Json = 1
  *
  *  scala> Json.jNumber(1).as[TrafficLight]
  *  res1: DecodeResult[TrafficLight] = DecodeResult(Right(Red))
  *
  *  scala> Json.jNumber(5).as[TrafficLight]
  *  res1: DecodeResult[TrafficLight] = DecodeResult(Left((5 is not a member of enum TrafficLight,CursorHistory(List()))))
  * }}}
  *
  * @tparam ValueType
  * @tparam EntryType
  */
sealed trait ArgonautValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {
  this: ValueEnum[ValueType, EntryType] =>

  implicit def argonautEncoder: EncodeJson[EntryType]
  implicit def argonautDecoder: DecodeJson[EntryType]

}

/**
  * ArgonautEnum for IntEnumEntry
  */
trait IntArgonautEnum[EntryType <: IntEnumEntry] extends ArgonautValueEnum[Int, EntryType] {
  this: ValueEnum[Int, EntryType] =>

  implicit val argonautEncoder: EncodeJson[EntryType] = Argonauter.encoder(this)
  implicit val argonautDecoder: DecodeJson[EntryType] = Argonauter.decoder(this)
}

/**
  * ArgonautEnum for LongEnumEntry
  */
trait LongArgonautEnum[EntryType <: LongEnumEntry] extends ArgonautValueEnum[Long, EntryType] {
  this: ValueEnum[Long, EntryType] =>

  implicit val argonautEncoder: EncodeJson[EntryType] = Argonauter.encoder(this)
  implicit val argonautDecoder: DecodeJson[EntryType] = Argonauter.decoder(this)
}

/**
  * ArgonautEnum for ShortEnumEntry
  */
trait ShortArgonautEnum[EntryType <: ShortEnumEntry] extends ArgonautValueEnum[Short, EntryType] {
  this: ValueEnum[Short, EntryType] =>

  implicit val argonautEncoder: EncodeJson[EntryType] = Argonauter.encoder(this)
  implicit val argonautDecoder: DecodeJson[EntryType] = Argonauter.decoder(this)
}

/**
  * ArgonautEnum for StringEnumEntry
  */
trait StringArgonautEnum[EntryType <: StringEnumEntry]
    extends ArgonautValueEnum[String, EntryType] { this: ValueEnum[String, EntryType] =>

  implicit val argonautEncoder: EncodeJson[EntryType] = Argonauter.encoder(this)
  implicit val argonautDecoder: DecodeJson[EntryType] = Argonauter.decoder(this)
}

/**
  * ArgonautEnum for CharEnumEntry
  */
trait CharArgonautEnum[EntryType <: CharEnumEntry] extends ArgonautValueEnum[Char, EntryType] {
  this: ValueEnum[Char, EntryType] =>

  implicit val argonautEncoder: EncodeJson[EntryType] = Argonauter.encoder(this)
  implicit val argonautDecoder: DecodeJson[EntryType] = Argonauter.decoder(this)
}

/**
  * ArgonautEnum for ByteEnumEntry
  */
trait ByteArgonautEnum[EntryType <: ByteEnumEntry] extends ArgonautValueEnum[Byte, EntryType] {
  this: ValueEnum[Byte, EntryType] =>

  implicit val argonautEncoder: EncodeJson[EntryType] = Argonauter.encoder(this)
  implicit val argonautDecoder: DecodeJson[EntryType] = Argonauter.decoder(this)
}
