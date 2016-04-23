package enumeratum.values

import reactivemongo.bson._

/**
 * @author Alessandro Lacava (@lambdista)
 * @since 2016-04-23
 */

trait ReactiveMongoBsonValueEnum[ValueType <: AnyVal, EntryType <: ValueEnumEntry[ValueType]] { enum: ValueEnum[ValueType, EntryType] =>
  implicit val bsonReaderShort = new BSONReader[BSONValue, Short] {
    override def read(bson: BSONValue): Short = bson match {
      case BSONInteger(x) => x.toShort
      case _ => throw new RuntimeException(s"Could not convert $bson to Short")
    }
  }

  implicit val bsonReaderInt = new BSONReader[BSONValue, Int] {
    override def read(bson: BSONValue): Int = bson match {
      case BSONInteger(x) => x
      case _ => throw new RuntimeException(s"Could not convert $bson to Int")
    }
  }

  implicit val bsonReaderLong = new BSONReader[BSONValue, Long] {
    override def read(bson: BSONValue): Long = bson match {
      case BSONLong(x) => x
      case _ => throw new RuntimeException(s"Could not convert $bson to Long")
    }
  }

  implicit val bsonWriterShort = new BSONWriter[Short, BSONValue] {
    override def write(t: Short): BSONValue = BSONInteger(t)
  }

  implicit val bsonWriterInt = new BSONWriter[Int, BSONValue] {
    override def write(t: Int): BSONValue = BSONInteger(t)
  }

  implicit val bsonWriterLong = new BSONWriter[Long, BSONValue] {
    override def write(t: Long): BSONValue = BSONLong(t)
  }

  /**
   * Implicit BSON handler for the entries of this enum
   */
  implicit def handler: BSONHandler[BSONValue, EntryType]

}

/**
 * Enum implementation for Int enum members that contains an implicit ReactiveMongo BSON Handler
 */
trait IntReactiveMongoBsonValueEnum[EntryType <: IntEnumEntry] extends ReactiveMongoBsonValueEnum[Int, EntryType] { this: IntEnum[EntryType] =>
  implicit val handler: BSONHandler[BSONValue, EntryType] = EnumHandler.handler(this)
}

/**
 * Enum implementation for Long enum members that contains an implicit ReactiveMongo BSON Handler
 */
trait LongReactiveMongoBsonValueEnum[EntryType <: LongEnumEntry] extends ReactiveMongoBsonValueEnum[Long, EntryType] { this: LongEnum[EntryType] =>
  implicit val handler: BSONHandler[BSONValue, EntryType] = EnumHandler.handler(this)
}

/**
 * Enum implementation for Short enum members that contains an implicit ReactiveMongo BSON Handler
 */
trait ShortReactiveMongoBsonValueEnum[EntryType <: ShortEnumEntry] extends ReactiveMongoBsonValueEnum[Short, EntryType] { this: ShortEnum[EntryType] =>
  implicit val handler: BSONHandler[BSONValue, EntryType] = EnumHandler.handler(this)
}
