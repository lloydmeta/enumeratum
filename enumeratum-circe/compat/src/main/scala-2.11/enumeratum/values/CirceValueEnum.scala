package enumeratum.values

import io.circe.{ Decoder, Encoder }

/**
 * Created by Lloyd on 4/14/16.
 *
 * Copyright 2016
 */

sealed trait CirceValueEnum[ValueType <: AnyVal, EntryType <: ValueEnumEntry[ValueType]] {
  this: ValueEnum[ValueType, EntryType] =>

  /**
   * Implicit Encoder for this enum
   */
  implicit def circeEncoder: Encoder[EntryType]

  /**
   * Implicit Decoder for this enum
   */
  implicit def circeDecoder: Decoder[EntryType]
}

/**
 * CirceEnum for IntEnumEntry
 */
trait IntCirceEnum[EntryType <: IntEnumEntry] extends CirceValueEnum[Int, EntryType] { this: ValueEnum[Int, EntryType] =>
  implicit val circeEncoder = Circe.encoder(this)
  implicit val circeDecoder = Circe.decoder(this)
}

/**
 * CirceEnum for LongEnumEntry
 */
trait LongCirceEnum[EntryType <: LongEnumEntry] extends CirceValueEnum[Long, EntryType] { this: ValueEnum[Long, EntryType] =>
  implicit val circeEncoder = Circe.encoder(this)
  implicit val circeDecoder = Circe.decoder(this)
}

/**
 * CirceEnum for ShortEnumEntry
 */
trait ShortCirceEnum[EntryType <: ShortEnumEntry] extends CirceValueEnum[Short, EntryType] { this: ValueEnum[Short, EntryType] =>
  implicit val circeEncoder = Circe.encoder(this)
  implicit val circeDecoder = Circe.decoder(this)
}
