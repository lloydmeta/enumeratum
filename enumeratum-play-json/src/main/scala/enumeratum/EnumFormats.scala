package enumeratum

import play.api.libs.json._

/**
 * Holds JSON reads and writes for [[enumeratum.Enum]]
 */
object EnumFormats {

  /**
   * Returns an Json Reads for a given enum [[Enum]]
   *
   * @param enum The enum
   * @param insensitive bind in a case-insensitive way, defaults to false
   */
  def reads[A <: EnumEntry](enum: Enum[A], insensitive: Boolean = false): Reads[A] =
    new Reads[A] {
      def reads(json: JsValue): JsResult[A] = json match {
        case JsString(s) => {
          val maybeBound =
            if (insensitive) enum.withNameInsensitiveOption(s)
            else enum.withNameOption(s)
          maybeBound match {
            case Some(obj) => JsSuccess(obj)
            case None => JsError("error.expected.validenumvalue")
          }
        }
        case _ => JsError("error.expected.enumstring")
      }
    }

  def readsLowercaseOnly[A <: EnumEntry](enum: Enum[A]): Reads[A] =
    new Reads[A] {
      def reads(json: JsValue): JsResult[A] = json match {
        case JsString(s) =>
          enum.withNameLowercaseOnlyOption(s) match {
            case Some(obj) => JsSuccess(obj)
            case None => JsError("error.expected.validenumvalue")
          }
        case _ => JsError("error.expected.enumstring")
      }
    }

  def readsUppercaseOnly[A <: EnumEntry](enum: Enum[A]): Reads[A] =
    new Reads[A] {
      def reads(json: JsValue): JsResult[A] = json match {
        case JsString(s) =>
          enum.withNameUppercaseOnlyOption(s) match {
            case Some(obj) => JsSuccess(obj)
            case None => JsError("error.expected.validenumvalue")
          }
        case _ => JsError("error.expected.enumstring")
      }
    }

  /**
   * Returns a Json writes for a given enum [[Enum]]
   */
  def writes[A <: EnumEntry](enum: Enum[A]): Writes[A] = new Writes[A] {
    def writes(v: A): JsValue = JsString(v.entryName)
  }

  /**
   * Returns a Json writes for a given enum [[Enum]] and transforms it to lower case
   */
  def writesLowercaseOnly[A <: EnumEntry](enum: Enum[A]): Writes[A] =
    new Writes[A] {
      def writes(v: A): JsValue = JsString(v.entryName.toLowerCase)
    }

  /**
   * Returns a Json writes for a given enum [[Enum]] and transforms it to upper case
   */
  def writesUppercaseOnly[A <: EnumEntry](enum: Enum[A]): Writes[A] =
    new Writes[A] {
      def writes(v: A): JsValue = JsString(v.entryName.toUpperCase)
    }

  /**
   * Returns a Json format for a given enum [[Enum]]
   *
   * @param enum The enum
   * @param insensitive bind in a case-insensitive way, defaults to false
   */
  def formats[A <: EnumEntry](enum: Enum[A], insensitive: Boolean = false): Format[A] = {
    Format(reads(enum, insensitive), writes(enum))
  }

  /**
   * Returns a Json format for a given enum [[Enum]] for handling lower case transformations
   *
   * @param enum The enum
   */
  def formatsLowerCaseOnly[A <: EnumEntry](enum: Enum[A]): Format[A] = {
    Format(readsLowercaseOnly(enum), writesLowercaseOnly(enum))
  }

  /**
   * Returns a Json format for a given enum [[Enum]] for handling upper case transformations
   *
   * @param enum The enum
   */
  def formatsUppercaseOnly[A <: EnumEntry](enum: Enum[A]): Format[A] = {
    Format(readsUppercaseOnly(enum), writesUppercaseOnly(enum))
  }

}
