package enumeratum

import reactivemongo.api.bson._

import scala.util.{Failure, Success, Try}

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
  ): BSONReader[A] = {
    if (insensitive) collect[A](enum.withNameInsensitiveOption)
    else collect[A](enum.withNameOption)
  }

  /**
    * Returns a KeyReader for a given enum [[Enum]]
    *
    * @param enum The enum
    * @param insensitive bind in a case-insensitive way, defaults to false
    */
  def keyReader[A <: EnumEntry](
      enum: Enum[A],
      insensitive: Boolean = false
  ): KeyReader[A] = {
    if (insensitive) collectKey[A](enum.withNameInsensitiveOption)
    else collectKey[A](enum.withNameOption)
  }

  /**
    * Returns a BSONReader for a given enum [[Enum]] transformed to lower case
    *
    * @param enum The enum
    */
  def readerLowercaseOnly[A <: EnumEntry](enum: Enum[A]): BSONReader[A] =
    collect[A](enum.withNameLowercaseOnlyOption)

  /**
    * Returns a KeyReader for a given enum [[Enum]] transformed to lower case
    *
    * @param enum The enum
    */
  def keyReaderLowercaseOnly[A <: EnumEntry](enum: Enum[A]): KeyReader[A] =
    collectKey[A](enum.withNameLowercaseOnlyOption)

  /**
    * Returns a BSONReader for a given enum [[Enum]] transformed to upper case
    *
    * @param enum The enum
    */
  def readerUppercaseOnly[A <: EnumEntry](enum: Enum[A]): BSONReader[A] =
    collect[A](enum.withNameUppercaseOnlyOption)

  /**
    * Returns a KeyReader for a given enum [[Enum]] transformed to upper case
    *
    * @param enum The enum
    */
  def keyReaderUppercaseOnly[A <: EnumEntry](enum: Enum[A]): KeyReader[A] =
    collectKey[A](enum.withNameUppercaseOnlyOption)

  private def collect[A](f: String => Option[A]): BSONReader[A] =
    BSONReader.option[A] {
      case BSONString(str) => f(str)
      case _               => None
    }

  private def collectKey[A](f: String => Option[A]): KeyReader[A] =
    KeyReader.from[A] { key =>
      f(key) match {
        case Some(a) => Success(a)
        case _       => Failure(exceptions.TypeDoesNotMatchException(key, "key"))
      }
    }

  /**
    * Returns a BSONWriter for a given enum [[Enum]]
    */
  def writer[A <: EnumEntry](enum: Enum[A]): BSONWriter[A] =
    BSONWriter[A] { t =>
      BSONString(t.entryName)
    }

  /**
    * Returns a KeyWriter for a given enum [[Enum]]
    */
  def keyWriter[A <: EnumEntry](enum: Enum[A]): KeyWriter[A] =
    KeyWriter[A](_.entryName)

  /**
    * Returns a BSONWriter for a given enum [[Enum]], outputting the value as lower case
    */
  def writerLowercase[A <: EnumEntry](enum: Enum[A]): BSONWriter[A] =
    BSONWriter[A] { t =>
      BSONString(t.entryName.toLowerCase)
    }

  /**
    * Returns a KeyWriter for a given enum [[Enum]],
    * outputting the value as lower case
    */
  def keyWriterLowercase[A <: EnumEntry](enum: Enum[A]): KeyWriter[A] =
    KeyWriter[A](_.entryName.toLowerCase)

  /**
    * Returns a BSONWriter for a given enum [[Enum]], outputting the value as upper case
    */
  def writerUppercase[A <: EnumEntry](enum: Enum[A]): BSONWriter[A] =
    BSONWriter[A] { t =>
      BSONString(t.entryName.toUpperCase)
    }

  /**
    * Returns a KeyWriter for a given enum [[Enum]],
    * outputting the value as upper case
    */
  def keyWriterUppercase[A <: EnumEntry](enum: Enum[A]): KeyWriter[A] =
    KeyWriter[A](_.entryName.toUpperCase)

  /**
    * Returns a BSONHandler for a given enum [[Enum]]
    *
    * @param enum The enum
    * @param insensitive bind in a case-insensitive way, defaults to false
    */
  def handler[A <: EnumEntry](
      enum: Enum[A],
      insensitive: Boolean = false
  ): BSONHandler[A] = BSONHandler.provided[A](reader(enum, insensitive), writer(enum))

  /**
    * Returns a BSONHandler for a given enum [[Enum]], handling a lower case transformation
    *
    * @param enum The enum
    */
  def handlerLowercaseOnly[A <: EnumEntry](enum: Enum[A]): BSONHandler[A] =
    BSONHandler.provided[A](readerLowercaseOnly(enum), writerLowercase(enum))

  /**
    * Returns a BSONHandler for a given enum [[Enum]], handling an upper case transformation
    *
    * @param enum The enum
    */
  def handlerUppercaseOnly[A <: EnumEntry](enum: Enum[A]): BSONHandler[A] =
    BSONHandler.provided[A](readerUppercaseOnly(enum), writerUppercase(enum))
}
