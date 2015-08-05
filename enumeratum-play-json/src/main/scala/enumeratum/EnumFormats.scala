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
  def reads[A <: EnumEntry](enum: Enum[A], insensitive: Boolean = false): Reads[A] = new Reads[A] {
    def reads(json: JsValue): JsResult[A] = json match {
      case JsString(s) => {
        val maybeBound = if (insensitive) enum.withNameInsensitiveOption(s) else enum.withNameOption(s)
        maybeBound match {
          case Some(obj) => JsSuccess(obj)
          case None => JsError(s"Enumeration expected of type: '$enum', but it does not appear to contain the value: '$s'")
        }
      }
      case _ => JsError("String value expected")
    }
  }

  /**
   * Returns a Json writes for a given enum [[Enum]]
   */
  def writes[A <: EnumEntry](enum: Enum[A]): Writes[A] = new Writes[A] {
    def writes(v: A): JsValue = JsString(v.entryName)
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

}
