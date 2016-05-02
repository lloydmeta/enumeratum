package enumeratum.values

import reactivemongo.bson._

import scala.util.{ Failure, Success, Try }

/**
 * @author Alessandro Lacava (@lambdista)
 * @since 2016-04-23
 */
object EnumHandler {
  /**
   * Returns a BSONReader for the provided ValueEnum based on the given base BSONReader for the Enum's value type
   */
  def reader[ValueType <: AnyVal, EntryType <: ValueEnumEntry[ValueType]]
    (enum: ValueEnum[ValueType, EntryType])
    (implicit
      baseBsonReader: BSONReader[BSONValue, ValueType]
    ): BSONReader[BSONValue, EntryType] = new BSONReader[BSONValue, EntryType] {
    override def read(bson: BSONValue): EntryType = {
      val result: Try[EntryType] = baseBsonReader.readTry(bson).flatMap { s =>
        val maybeBound = enum.withValueOpt(s)
        maybeBound match {
          case Some(obj) => Success(obj)
          case None => Failure(
            new RuntimeException(s"Enumeration expected of type: '$enum', but it does not appear to contain the value: '$s'")
          )
        }
      }

      result.get
    }
  }

  /**
   * Returns a BSONWriter for the provided ValueEnum based on the given base BSONWriter for the Enum's value type
   */
  def writer[ValueType <: AnyVal, EntryType <: ValueEnumEntry[ValueType]]
    (enum: ValueEnum[ValueType, EntryType])
    (implicit
      baseBsonWriter: BSONWriter[ValueType, BSONValue]
    ): BSONWriter[EntryType, BSONValue] = new BSONWriter[EntryType, BSONValue] {
    override def write(t: EntryType): BSONValue = baseBsonWriter.write(t.value)
  }

  /**
   * Returns a BSONHandler for the provided ValueEnum based on the given
   * base BSONReader and BSONWriter for the Enum's value type
   */
  def handler[ValueType <: AnyVal, EntryType <: ValueEnumEntry[ValueType]]
    (enum: ValueEnum[ValueType, EntryType])
    (implicit
       baseBsonReader: BSONReader[BSONValue, ValueType],
       baseBsonWriter: BSONWriter[ValueType, BSONValue]
    ): BSONHandler[BSONValue, EntryType] = new BSONHandler[BSONValue, EntryType] {
    override def read(bson: BSONValue): EntryType = reader(enum).read(bson)

    override def write(t: EntryType): BSONValue = writer(enum).write(t)
  }
}