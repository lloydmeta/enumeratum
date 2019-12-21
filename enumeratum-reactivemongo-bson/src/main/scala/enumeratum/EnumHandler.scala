package enumeratum

import reactivemongo.api.bson._

import scala.util.{Failure, Try}

/**
  * Holds BSON reader and writer for [[enumeratum.Enum]]
  *
  * @author Alessandro Lacava (@lambdista)
  * @since 2016-04-23
  */
@SuppressWarnings(Array("org.wartremover.warts.Throw"))
object EnumHandler {

  /**
    * Returns a BSONReader for a given enum [[Enum]]
    *
    * @param enum The enum
    * @param insensitive bind in a case-insensitive way, defaults to false
    */
  def reader[A <: EnumEntry](
      enum: Enum[A],
      insensitive: Boolean = false
  ): BSONReader[A] =
    new BSONReader[A] {
      override def readTry(bson: BSONValue): Try[A] =
        bson match {
          case BSONString(s) if insensitive => Try(enum.withNameInsensitive(s))
          case BSONString(s)                => Try(enum.withName(s))
          case _                            => Failure(new RuntimeException("String value expected"))
        }
    }

  /**
    * Returns a BSONReader for a given enum [[Enum]] transformed to lower case
    *
    * @param enum The enum
    */
  def readerLowercaseOnly[A <: EnumEntry](enum: Enum[A]): BSONReader[A] = new BSONReader[A] {
    override def readTry(bson: BSONValue): Try[A] = bson match {
      case BSONString(s) => Try(enum.withNameLowercaseOnly(s))
      case _             => Failure(new RuntimeException("String value expected"))
    }
  }

  /**
    * Returns a BSONReader for a given enum [[Enum]] transformed to upper case
    *
    * @param enum The enum
    */
  def readerUppercaseOnly[A <: EnumEntry](enum: Enum[A]): BSONReader[A] = new BSONReader[A] {
    override def readTry(bson: BSONValue): Try[A] =
      bson match {
        case BSONString(s) => Try(enum.withNameUppercaseOnly(s))
        case _             => Failure(new RuntimeException("String value expected"))
      }
  }

  /**
    * Returns a BSONWriter for a given enum [[Enum]]
    */
  def writer[A <: EnumEntry](enum: Enum[A]): BSONWriter[A] = new BSONWriter[A] {
    override def writeTry(t: A): Try[BSONValue] = Try(BSONString(t.entryName))
  }

  /**
    * Returns a BSONWriter for a given enum [[Enum]], outputting the value as lower case
    */
  def writerLowercase[A <: EnumEntry](enum: Enum[A]): BSONWriter[A] = new BSONWriter[A] {
    override def writeTry(t: A): Try[BSONValue] = Try(BSONString(t.entryName.toLowerCase))
  }

  /**
    * Returns a BSONWriter for a given enum [[Enum]], outputting the value as upper case
    */
  def writerUppercase[A <: EnumEntry](enum: Enum[A]): BSONWriter[A] =
    new BSONWriter[A] {
      override def writeTry(t: A): Try[BSONValue] = Try(BSONString(t.entryName.toUpperCase))
    }

  /**
    * Returns a BSONHandler for a given enum [[Enum]]
    *
    * @param enum The enum
    * @param insensitive bind in a case-insensitive way, defaults to false
    */
  def handler[A <: EnumEntry](
      enum: Enum[A],
      insensitive: Boolean = false
  ): BSONHandler[A] =
    new BSONHandler[A] {
      private val concreteReader = reader(enum, insensitive)
      private val concreteWriter = writer(enum)

      override def readTry(bson: BSONValue): Try[A] = concreteReader.readTry(bson)

      override def writeTry(t: A): Try[BSONValue] = concreteWriter.writeTry(t)
    }

  /**
    * Returns a BSONHandler for a given enum [[Enum]], handling a lower case transformation
    *
    * @param enum The enum
    */
  def handlerLowercaseOnly[A <: EnumEntry](enum: Enum[A]): BSONHandler[A] =
    new BSONHandler[A] {
      private val concreteReader = readerLowercaseOnly(enum)
      private val concreteWriter = writerLowercase(enum)

      override def readTry(bson: BSONValue): Try[A] = concreteReader.readTry(bson)

      override def writeTry(t: A): Try[BSONValue] = concreteWriter.writeTry(t)
    }

  /**
    * Returns a BSONHandler for a given enum [[Enum]], handling an upper case transformation
    *
    * @param enum The enum
    */
  def handlerUppercaseOnly[A <: EnumEntry](enum: Enum[A]): BSONHandler[A] =
    new BSONHandler[A] {
      private val concreteReader = readerUppercaseOnly(enum)
      private val concreteWriter = writerUppercase(enum)

      override def readTry(bson: BSONValue): Try[A] = concreteReader.readTry(bson)

      override def writeTry(t: A): Try[BSONValue] = concreteWriter.writeTry(t)
    }
}
