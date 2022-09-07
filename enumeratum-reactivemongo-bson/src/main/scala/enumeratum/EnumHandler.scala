package enumeratum

import reactivemongo.api.bson._

import scala.util.{Failure, Success}

/** Holds BSON reader and writer for [[enumeratum.Enum]]
  *
  * @author
  *   Alessandro Lacava (@lambdista)
  * @since 2016-04-23
  */
@SuppressWarnings(Array("org.wartremover.warts.Throw"))
object EnumHandler {

  /** Returns a BSONReader for a given enum [[Enum]]
    *
    * @param e
    *   The enum
    * @param insensitive
    *   bind in a case-insensitive way, defaults to false
    */
  def reader[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A],
      insensitive: Boolean = false
  ): BSONReader[A] = {
    if (insensitive) collect[A](e.withNameInsensitiveOption)
    else collect[A](e.withNameOption)
  }

  /** Returns a KeyReader for a given enum [[Enum]]
    *
    * @param e
    *   The enum
    * @param insensitive
    *   bind in a case-insensitive way, defaults to false
    */
  def keyReader[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A],
      insensitive: Boolean = false
  ): KeyReader[A] = {
    if (insensitive) collectKey[A](e.withNameInsensitiveOption)
    else collectKey[A](e.withNameOption)
  }

  /** Returns a BSONReader for a given enum [[Enum]] transformed to lower case
    *
    * @param e
    *   The enum
    */
  def readerLowercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): BSONReader[A] =
    collect[A](e.withNameLowercaseOnlyOption)

  /** Returns a KeyReader for a given enum [[Enum]] transformed to lower case
    *
    * @param e
    *   The enum
    */
  def keyReaderLowercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): KeyReader[A] =
    collectKey[A](e.withNameLowercaseOnlyOption)

  /** Returns a BSONReader for a given enum [[Enum]] transformed to upper case
    *
    * @param e
    *   The enum
    */
  def readerUppercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): BSONReader[A] =
    collect[A](e.withNameUppercaseOnlyOption)

  /** Returns a KeyReader for a given enum [[Enum]] transformed to upper case
    *
    * @param e
    *   The enum
    */
  def keyReaderUppercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): KeyReader[A] =
    collectKey[A](e.withNameUppercaseOnlyOption)

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

  /** Returns a BSONWriter for a given enum [[Enum]]
    */
  def writer[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): BSONWriter[A] =
    BSONWriter[A] { t =>
      BSONString(t.entryName)
    }

  /** Returns a KeyWriter for a given enum [[Enum]]
    */
  def keyWriter[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): KeyWriter[A] =
    KeyWriter[A](_.entryName)

  /** Returns a BSONWriter for a given enum [[Enum]], outputting the value as lower case
    */
  def writerLowercase[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): BSONWriter[A] =
    BSONWriter[A] { t =>
      BSONString(t.entryName.toLowerCase)
    }

  /** Returns a KeyWriter for a given enum [[Enum]], outputting the value as lower case
    */
  def keyWriterLowercase[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): KeyWriter[A] =
    KeyWriter[A](_.entryName.toLowerCase)

  /** Returns a BSONWriter for a given enum [[Enum]], outputting the value as upper case
    */
  def writerUppercase[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): BSONWriter[A] =
    BSONWriter[A] { t =>
      BSONString(t.entryName.toUpperCase)
    }

  /** Returns a KeyWriter for a given enum [[Enum]], outputting the value as upper case
    */
  def keyWriterUppercase[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): KeyWriter[A] =
    KeyWriter[A](_.entryName.toUpperCase)

  /** Returns a BSONHandler for a given enum [[Enum]]
    *
    * @param e
    *   The enum
    * @param insensitive
    *   bind in a case-insensitive way, defaults to false
    */
  def handler[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A],
      insensitive: Boolean = false
  ): BSONHandler[A] = BSONHandler.provided[A](reader(e, insensitive), writer(e))

  /** Returns a BSONHandler for a given enum [[Enum]], handling a lower case transformation
    *
    * @param e
    *   The enum
    */
  def handlerLowercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): BSONHandler[A] =
    BSONHandler.provided[A](readerLowercaseOnly(e), writerLowercase(e))

  /** Returns a BSONHandler for a given enum [[Enum]], handling an upper case transformation
    *
    * @param e
    *   The enum
    */
  def handlerUppercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): BSONHandler[A] =
    BSONHandler.provided[A](readerUppercaseOnly(e), writerUppercase(e))
}
