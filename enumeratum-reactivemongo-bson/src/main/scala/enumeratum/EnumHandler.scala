package enumeratum

import reactivemongo.bson._

import scala.util.{Failure, Success}

/**
  * Holds BSON reader and writer for [[enumeratum.Enum]]
  *
  * @author Alessandro Lacava (@lambdista)
  * @since 2016-04-23
  */
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
  ): BSONReader[BSONValue, A] =
    new BSONReader[BSONValue, A] {
      override def read(bson: BSONValue): A = {
        bson match {
          case BSONString(s) if insensitive => enum.withNameInsensitive(s)
          case BSONString(s)                => enum.withName(s)
          case _                            => throw new RuntimeException("String value expected")
        }
      }
    }

  /**
    * Returns a BSONReader for a given enum [[Enum]] transformed to lower case
    *
    * @param enum The enum
    */
  def readerLowercaseOnly[A <: EnumEntry](enum: Enum[A]): BSONReader[BSONValue, A] =
    new BSONReader[BSONValue, A] {
      override def read(bson: BSONValue): A = {
        bson match {
          case BSONString(s) => enum.withNameLowercaseOnly(s)
          case _             => throw new RuntimeException("String value expected")
        }
      }
    }

  /**
    * Returns a BSONReader for a given enum [[Enum]] transformed to upper case
    *
    * @param enum The enum
    */
  def readerUppercaseOnly[A <: EnumEntry](enum: Enum[A]): BSONReader[BSONValue, A] =
    new BSONReader[BSONValue, A] {
      override def read(bson: BSONValue): A = {
        bson match {
          case BSONString(s) => enum.withNameUppercaseOnly(s)
          case _             => throw new RuntimeException("String value expected")
        }
      }
    }

  /**
    * Returns a BSONWriter for a given enum [[Enum]]
    */
  def writer[A <: EnumEntry](enum: Enum[A]): BSONWriter[A, BSONValue] =
    new BSONWriter[A, BSONValue] {
      override def write(t: A): BSONValue = BSONString(t.entryName)
    }

  /**
    * Returns a BSONWriter for a given enum [[Enum]], outputting the value as lower case
    */
  def writerLowercase[A <: EnumEntry](enum: Enum[A]): BSONWriter[A, BSONValue] =
    new BSONWriter[A, BSONValue] {
      override def write(t: A): BSONValue = BSONString(t.entryName.toLowerCase)
    }

  /**
    * Returns a BSONWriter for a given enum [[Enum]], outputting the value as upper case
    */
  def writerUppercase[A <: EnumEntry](enum: Enum[A]): BSONWriter[A, BSONValue] =
    new BSONWriter[A, BSONValue] {
      override def write(t: A): BSONValue = BSONString(t.entryName.toUpperCase)
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
  ): BSONHandler[BSONValue, A] =
    new BSONHandler[BSONValue, A] {
      val concreteReader = reader(enum, insensitive)
      val concreteWriter = writer(enum)

      override def read(bson: BSONValue): A = concreteReader.read(bson)

      override def write(t: A): BSONValue = concreteWriter.write(t)
    }

  /**
    * Returns a BSONHandler for a given enum [[Enum]], handling a lower case transformation
    *
    * @param enum The enum
    */
  def handlerLowercaseOnly[A <: EnumEntry](enum: Enum[A]): BSONHandler[BSONValue, A] =
    new BSONHandler[BSONValue, A] {
      val concreteReader = readerLowercaseOnly(enum)
      val concreteWriter = writerLowercase(enum)

      override def read(bson: BSONValue): A = concreteReader.read(bson)

      override def write(t: A): BSONValue = concreteWriter.write(t)
    }

  /**
    * Returns a BSONHandler for a given enum [[Enum]], handling an upper case transformation
    *
    * @param enum The enum
    */
  def handlerUppercaseOnly[A <: EnumEntry](enum: Enum[A]): BSONHandler[BSONValue, A] =
    new BSONHandler[BSONValue, A] {
      val concreteReader = readerUppercaseOnly(enum)
      val concreteWriter = writerUppercase(enum)

      override def read(bson: BSONValue): A = concreteReader.read(bson)

      override def write(t: A): BSONValue = concreteWriter.write(t)
    }
}
