package enumeratum.values

import reactivemongo.api.bson.{BSONHandler, BSONReader, BSONValue, BSONWriter}

import scala.util.Try

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
      implicit baseBsonReader: BSONReader[ValueType]
  ): BSONReader[EntryType] = new BSONReader[EntryType] {
    override def readTry(bson: BSONValue): Try[EntryType] =
      baseBsonReader.readTry(bson).map(enum.withValue)
  }

  /**
    * Returns a BSONWriter for the provided ValueEnum based on the given base BSONWriter for the Enum's value type
    */
  def writer[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]
  )(
      implicit baseBsonWriter: BSONWriter[ValueType]
  ): BSONWriter[EntryType] = new BSONWriter[EntryType] {

    override def writeTry(t: EntryType): Try[BSONValue] = baseBsonWriter.writeTry(t.value)
  }

  /**
    * Returns a BSONHandler for the provided ValueEnum based on the given base BSONReader and BSONWriter for the
    * Enum's value type
    */
  def handler[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType])(
      implicit baseBsonHandler: BSONHandler[ValueType]
  ): BSONHandler[EntryType] = new BSONHandler[EntryType] {
    private val concreteReader = reader(enum)
    private val concreteWriter = writer(enum)

    override def readTry(bson: BSONValue): Try[EntryType] = concreteReader.readTry(bson)

    override def writeTry(t: EntryType): Try[BSONValue] = concreteWriter.writeTry(t)
  }
}
