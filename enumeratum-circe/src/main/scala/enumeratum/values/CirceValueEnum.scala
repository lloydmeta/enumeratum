package enumeratum.values

import io.circe.{Decoder, Encoder}

/**
  * Created by Lloyd on 4/14/16.
  *
  * Copyright 2016
  */
sealed trait CirceValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {
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
trait IntCirceEnum[EntryType <: IntEnumEntry] extends CirceValueEnum[Int, EntryType] {
  this: ValueEnum[Int, EntryType] =>
  implicit val circeEncoder = Circe.encoder(this)
  implicit val circeDecoder = Circe.decoder(this)
}

/**
  * CirceEnum for LongEnumEntry
  */
trait LongCirceEnum[EntryType <: LongEnumEntry] extends CirceValueEnum[Long, EntryType] {
  this: ValueEnum[Long, EntryType] =>
  implicit val circeEncoder = Circe.encoder(this)
  implicit val circeDecoder = Circe.decoder(this)
}

/**
  * CirceEnum for ShortEnumEntry
  */
trait ShortCirceEnum[EntryType <: ShortEnumEntry] extends CirceValueEnum[Short, EntryType] {
  this: ValueEnum[Short, EntryType] =>
  implicit val circeEncoder = Circe.encoder(this)
  implicit val circeDecoder = Circe.decoder(this)
}

/**
  * CirceEnum for StringEnumEntry
  */
trait StringCirceEnum[EntryType <: StringEnumEntry] extends CirceValueEnum[String, EntryType] {
  this: ValueEnum[String, EntryType] =>
  implicit val circeEncoder = Circe.encoder(this)
  implicit val circeDecoder = Circe.decoder(this)
}

/**
  * CirceEnum for CharEnumEntry
  */
trait CharCirceEnum[EntryType <: CharEnumEntry] extends CirceValueEnum[Char, EntryType] {
  this: ValueEnum[Char, EntryType] =>
  implicit val circeEncoder = Circe.encoder(this)
  implicit val circeDecoder = Circe.decoder(this)
}

/**
  * CirceEnum for ByteEnumEntry
  */
trait ByteCirceEnum[EntryType <: ByteEnumEntry] extends CirceValueEnum[Byte, EntryType] {
  this: ValueEnum[Byte, EntryType] =>
  implicit val circeEncoder = Circe.encoder(this)
  implicit val circeDecoder = Circe.decoder(this)
}
