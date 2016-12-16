package enumeratum.values

import reactivemongo.bson._

/**
  * @author Alessandro Lacava (@lambdista)
  * @since 2016-04-23
  */
object EnumHandler {

  /**
    * Returns a BSONReader for the provided ValueEnum based on the given base BSONReader for the Enum's value type
    */
  def reader[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]
  )(
      implicit baseBsonReader: BSONReader[BSONValue, ValueType]
  ): BSONReader[BSONValue, EntryType] = new BSONReader[BSONValue, EntryType] {
    def read(bson: BSONValue): EntryType = {
      val value = baseBsonReader.read(bson)
      enum.withValue(value)
    }
  }

  /**
    * Returns a BSONWriter for the provided ValueEnum based on the given base BSONWriter for the Enum's value type
    */
  def writer[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]
  )(
      implicit baseBsonWriter: BSONWriter[ValueType, BSONValue]
  ): BSONWriter[EntryType, BSONValue] = new BSONWriter[EntryType, BSONValue] {
    def write(t: EntryType): BSONValue = baseBsonWriter.write(t.value)
  }

  /**
    * Returns a BSONHandler for the provided ValueEnum based on the given base BSONReader and BSONWriter for the
    * Enum's value type
    */
  def handler[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]
  )(
      implicit baseBsonHandler: BSONHandler[BSONValue, ValueType]
  ): BSONHandler[BSONValue, EntryType] =
    new BSONHandler[BSONValue, EntryType] {
      private val concreteReader           = reader(enum)
      private val concreteWriter           = writer(enum)
      def read(bson: BSONValue): EntryType = concreteReader.read(bson)
      def write(t: EntryType): BSONValue   = concreteWriter.write(t)
    }
}
