package enumeratum

import play.api.libs.json._

/** Holds JSON reads and writes for [[enumeratum.Enum]]
  */
object EnumFormats {

  /** Returns an Json Reads for a given enum [[Enum]]
    *
    * @param enum
    *   The enum
    * @param insensitive
    *   bind in a case-insensitive way, defaults to false
    */
  def reads[A <: EnumEntry](enum: Enum[A], insensitive: Boolean = false): Reads[A] =
    readsAndExtracts[A](enum) { s =>
      if (insensitive) enum.withNameInsensitiveOption(s)
      else enum.withNameOption(s)
    }

  def readsLowercaseOnly[A <: EnumEntry](enum: Enum[A]): Reads[A] =
    readsAndExtracts[A](enum)(enum.withNameLowercaseOnlyOption)

  def readsUppercaseOnly[A <: EnumEntry](enum: Enum[A]): Reads[A] =
    readsAndExtracts[A](enum)(enum.withNameUppercaseOnlyOption)

  def keyReads[A <: EnumEntry](enum: Enum[A], insensitive: Boolean = false): KeyReads[A] =
    readsKeyAndExtracts[A](enum) { s =>
      if (insensitive) enum.withNameInsensitiveOption(s)
      else enum.withNameOption(s)
    }

  def keyReadsLowercaseOnly[A <: EnumEntry](enum: Enum[A]): KeyReads[A] =
    readsKeyAndExtracts[A](enum)(enum.withNameLowercaseOnlyOption)

  def keyReadsUppercaseOnly[A <: EnumEntry](enum: Enum[A]): KeyReads[A] =
    readsKeyAndExtracts[A](enum)(enum.withNameUppercaseOnlyOption)

  /** Returns a Json writes for a given enum [[Enum]]
    */
  def writes[A <: EnumEntry](enum: Enum[A]): Writes[A] = Writes[A] { e =>
    JsString(e.entryName)
  }

  /** Returns a Json writes for a given enum [[Enum]] and transforms it to lower case
    */
  def writesLowercaseOnly[A <: EnumEntry](enum: Enum[A]): Writes[A] =
    Writes[A] { e =>
      JsString(e.entryName.toLowerCase)
    }

  /** Returns a Json writes for a given enum [[Enum]] and transforms it to upper case
    */
  def writesUppercaseOnly[A <: EnumEntry](enum: Enum[A]): Writes[A] =
    Writes[A] { e =>
      JsString(e.entryName.toUpperCase)
    }

  /** Returns a Json key writes for a given enum [[Enum]]
    */
  def keyWrites[A <: EnumEntry](enum: Enum[A]): KeyWrites[A] =
    new KeyWrites[A] {
      def writeKey(e: A): String = e.entryName
    }

  /** Returns a Json key writes for a given enum [[Enum]] and transforms it to lower case
    */
  def keyWritesLowercaseOnly[A <: EnumEntry](enum: Enum[A]): KeyWrites[A] =
    new KeyWrites[A] {
      def writeKey(e: A) = e.entryName.toLowerCase
    }

  /** Returns a Json key writes for a given enum [[Enum]] and transforms it to upper case
    */
  def keyWritesUppercaseOnly[A <: EnumEntry](enum: Enum[A]): KeyWrites[A] =
    new KeyWrites[A] {
      def writeKey(e: A) = e.entryName.toUpperCase
    }

  /** Returns a Json format for a given enum [[Enum]]
    *
    * @param enum
    *   The enum
    * @param insensitive
    *   bind in a case-insensitive way, defaults to false
    */
  def formats[A <: EnumEntry](enum: Enum[A], insensitive: Boolean = false): Format[A] = {
    Format(reads(enum, insensitive), writes(enum))
  }

  /** Returns a Json format for a given enum [[Enum]] for handling lower case transformations
    *
    * @param enum
    *   The enum
    */
  def formatsLowerCaseOnly[A <: EnumEntry](enum: Enum[A]): Format[A] = {
    Format(readsLowercaseOnly(enum), writesLowercaseOnly(enum))
  }

  /** Returns a Json format for a given enum [[Enum]] for handling upper case transformations
    *
    * @param enum
    *   The enum
    */
  def formatsUppercaseOnly[A <: EnumEntry](enum: Enum[A]): Format[A] = {
    Format(readsUppercaseOnly(enum), writesUppercaseOnly(enum))
  }

  // ---

  private def readsAndExtracts[A <: EnumEntry](
      enum: Enum[A]
  )(extract: String => Option[A]): Reads[A] = Reads[A] {
    case JsString(s) =>
      extract(s) match {
        case Some(obj) => JsSuccess(obj)
        case None      => JsError("error.expected.validenumvalue")
      }

    case _ => JsError("error.expected.enumstring")
  }

  private def readsKeyAndExtracts[A <: EnumEntry](
      enum: Enum[A]
  )(extract: String => Option[A]): KeyReads[A] = new KeyReads[A] {
    def readKey(s: String): JsResult[A] = extract(s) match {
      case Some(obj) => JsSuccess(obj)
      case None      => JsError("error.expected.validenumvalue")
    }
  }
}
