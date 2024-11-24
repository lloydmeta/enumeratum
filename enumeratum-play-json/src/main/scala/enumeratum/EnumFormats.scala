package enumeratum

import play.api.libs.json._

/** Holds JSON reads and writes for [[enumeratum.Enum]]
  */
object EnumFormats {

  /** Returns an Json Reads for a given enum [[Enum]]
    *
    * @param e
    *   The enum
    * @param insensitive
    *   bind in a case-insensitive way, defaults to false
    */
  def reads[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A],
      insensitive: Boolean = false,
      detailedError: Boolean = false
  ): Reads[A] =
    readsAndExtracts[A](e, detailedError) { s =>
      if (insensitive) e.withNameInsensitiveOption(s)
      else e.withNameOption(s)
    }

  def readsLowercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A],
      detailedError: Boolean = false
  ): Reads[A] =
    readsAndExtracts[A](e, detailedError)(e.withNameLowercaseOnlyOption)

  def readsUppercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A],
      detailedError: Boolean = false
  ): Reads[A] =
    readsAndExtracts[A](e, detailedError)(e.withNameUppercaseOnlyOption)

  def keyReads[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A],
      insensitive: Boolean = false,
      detailedError: Boolean = false
  ): KeyReads[A] =
    readsKeyAndExtracts[A](e, detailedError) { s =>
      if (insensitive) e.withNameInsensitiveOption(s)
      else e.withNameOption(s)
    }

  def keyReadsLowercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A],
      detailedError: Boolean = false
  ): KeyReads[A] =
    readsKeyAndExtracts[A](e, detailedError)(e.withNameLowercaseOnlyOption)

  def keyReadsUppercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A],
      detailedError: Boolean = false
  ): KeyReads[A] =
    readsKeyAndExtracts[A](e, detailedError)(e.withNameUppercaseOnlyOption)

  /** Returns a Json writes for a given enum [[Enum]]
    */
  def writes[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): Writes[A] = Writes[A] {
    e =>
      JsString(e.entryName)
  }

  /** Returns a Json writes for a given enum [[Enum]] and transforms it to lower case
    */
  def writesLowercaseOnly[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): Writes[A] =
    Writes[A] { e =>
      JsString(e.entryName.toLowerCase)
    }

  /** Returns a Json writes for a given enum [[Enum]] and transforms it to upper case
    */
  def writesUppercaseOnly[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): Writes[A] =
    Writes[A] { e =>
      JsString(e.entryName.toUpperCase)
    }

  /** Returns a Json key writes for a given enum [[Enum]]
    */
  def keyWrites[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): KeyWrites[A] =
    new KeyWrites[A] {
      def writeKey(e: A): String = e.entryName
    }

  /** Returns a Json key writes for a given enum [[Enum]] and transforms it to lower case
    */
  def keyWritesLowercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): KeyWrites[A] =
    new KeyWrites[A] {
      def writeKey(e: A) = e.entryName.toLowerCase
    }

  /** Returns a Json key writes for a given enum [[Enum]] and transforms it to upper case
    */
  def keyWritesUppercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): KeyWrites[A] =
    new KeyWrites[A] {
      def writeKey(e: A) = e.entryName.toUpperCase
    }

  /** Returns a Json format for a given enum [[Enum]]
    *
    * @param e
    *   The enum
    * @param insensitive
    *   bind in a case-insensitive way, defaults to false
    */
  def formats[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A],
      insensitive: Boolean = false
  ): Format[A] = {
    Format(reads(e, insensitive), writes(e))
  }

  /** Returns a Json format for a given enum [[Enum]] for handling lower case transformations
    *
    * @param e
    *   The enum
    */
  def formatsLowerCaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): Format[A] = {
    Format(readsLowercaseOnly(e), writesLowercaseOnly(e))
  }

  /** Returns a Json format for a given enum [[Enum]] for handling upper case transformations
    *
    * @param e
    *   The enum
    */
  def formatsUppercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): Format[A] = {
    Format(readsUppercaseOnly(e), writesUppercaseOnly(e))
  }

  // ---

  private def readsAndExtracts[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A],
      detailedError: Boolean = false
  )(extract: String => Option[A]): Reads[A] = Reads[A] {
    case JsString(s) =>
      extract(s) match {
        case Some(obj) => JsSuccess(obj)
        case None if detailedError =>
          JsError(
            JsonValidationError(
              "error.expected.validenumvalue",
              s"valid enum values are: (${e.values.map(_.entryName).mkString(", ")}), but provided: $s"
            )
          )
        case None => JsError("error.expected.validenumvalue")
      }

    case _ => JsError("error.expected.enumstring")
  }

  private def readsKeyAndExtracts[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A],
      detailedError: Boolean = false
  )(extract: String => Option[A]): KeyReads[A] = new KeyReads[A] {
    def readKey(s: String): JsResult[A] = extract(s) match {
      case Some(obj) => JsSuccess(obj)
      case None if detailedError =>
        JsError(
          JsonValidationError(
            "error.expected.validenumvalue",
            s"valid enum values are: (${e.values.map(_.entryName).mkString(", ")}), but provided: $s"
          )
        )
      case None => JsError("error.expected.validenumvalue")
    }
  }
}
