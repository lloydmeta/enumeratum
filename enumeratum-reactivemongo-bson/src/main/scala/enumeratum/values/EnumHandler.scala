package enumeratum.values

import reactivemongo.api.bson._

/**
  * @author Alessandro Lacava (@lambdista)
  * @since 2016-04-23
  */
object EnumHandler {

  /**
    * Returns a BSONReader for the provided ValueEnum based on
    * the given base BSONReader for the Enum's value type.
    */
  def reader[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]
  )(
      implicit baseBsonReader: BSONReader[ValueType]
  ): BSONReader[EntryType] = BSONReader.from[EntryType] { bson =>
    baseBsonReader.readTry(bson).map(enum.withValue)
  }

  /**
    * Returns a KeyReader for the provided ValueEnum based on
    * the given base KeyReader for the Enum's value type.
    */
  def keyReader[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]
  )(
      implicit baseBsonReader: KeyReader[ValueType]
  ): KeyReader[EntryType] = KeyReader.from[EntryType] { bson =>
    baseBsonReader.readTry(bson).map(enum.withValue)
  }

  /**
    * Returns a BSONWriter for the provided ValueEnum based on
    * the given base BSONWriter for the Enum's value type.
    */
  def writer[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]
  )(
      implicit baseBsonWriter: BSONWriter[ValueType]
  ): BSONWriter[EntryType] = BSONWriter.from[EntryType] { t =>
    baseBsonWriter.writeTry(t.value)
  }

  /**
    * Returns a KeyWriter for the provided ValueEnum based on
    * the given base KeyWriter for the Enum's value type.
    */
  def keyWriter[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]
  )(
      implicit baseBsonWriter: KeyWriter[ValueType]
  ): KeyWriter[EntryType] = KeyWriter.from[EntryType] { t =>
    baseBsonWriter.writeTry(t.value)
  }

  /**
    * Returns a BSONHandler for the provided ValueEnum based on
    * the given base BSONReader and BSONWriter for the Enum's value type.
    */
  def handler[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType])(
      implicit baseBsonHandler: BSONHandler[ValueType]
  ): BSONHandler[EntryType] = BSONHandler.provided[EntryType](reader(enum), writer(enum))
}
