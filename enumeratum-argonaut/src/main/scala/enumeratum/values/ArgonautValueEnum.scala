package enumeratum.values

import argonaut._

/**
  * Created by alonsodomin on 14/10/2016.
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

  implicit val argonautEncoder = Argonauter.encoder(this)
  implicit val argonautDecoder = Argonauter.decoder(this)
}

/**
  * ArgonautEnum for LongEnumEntry
  */
trait LongArgonautEnum[EntryType <: LongEnumEntry] extends ArgonautValueEnum[Long, EntryType] {
  this: ValueEnum[Long, EntryType] =>

  implicit val argonautEncoder = Argonauter.encoder(this)
  implicit val argonautDecoder = Argonauter.decoder(this)
}

/**
  * ArgonautEnum for ShortEnumEntry
  */
trait ShortArgonautEnum[EntryType <: ShortEnumEntry] extends ArgonautValueEnum[Short, EntryType] {
  this: ValueEnum[Short, EntryType] =>

  implicit val argonautEncoder = Argonauter.encoder(this)
  implicit val argonautDecoder = Argonauter.decoder(this)
}

/**
  * ArgonautEnum for StringEnumEntry
  */
trait StringArgonautEnum[EntryType <: StringEnumEntry]
    extends ArgonautValueEnum[String, EntryType] { this: ValueEnum[String, EntryType] =>

  implicit val argonautEncoder = Argonauter.encoder(this)
  implicit val argonautDecoder = Argonauter.decoder(this)
}

/**
  * ArgonautEnum for CharEnumEntry
  */
trait CharArgonautEnum[EntryType <: CharEnumEntry] extends ArgonautValueEnum[Char, EntryType] {
  this: ValueEnum[Char, EntryType] =>

  implicit val argonautEncoder = Argonauter.encoder(this)
  implicit val argonautDecoder = Argonauter.decoder(this)
}

/**
  * ArgonautEnum for ByteEnumEntry
  */
trait ByteArgonautEnum[EntryType <: ByteEnumEntry] extends ArgonautValueEnum[Byte, EntryType] {
  this: ValueEnum[Byte, EntryType] =>

  implicit val argonautEncoder = Argonauter.encoder(this)
  implicit val argonautDecoder = Argonauter.decoder(this)
}
