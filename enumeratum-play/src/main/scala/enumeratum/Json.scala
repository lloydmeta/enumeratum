package enumeratum

import play.api.libs.json._

import scala.util.control.NonFatal

/**
 * Holds JSON reads and writes for [[enumeratum.Enum]]
 */
object Json {

  /**
   * Returns an Json Reads for a given enum [[Enum]]
   */
  def reads[A](enum: Enum[A]): Reads[A] = new Reads[A] {
    def reads(json: JsValue): JsResult[A] = json match {
      case JsString(s) => {
        try {
          JsSuccess(enum.withName(s))
        } catch {
          case NonFatal(e) => JsError(s"Enumeration expected of type: '$enum', but it does not appear to contain the value: '$s'")
        }
      }
      case _ => JsError("String value expected")
    }
  }

  /**
   * Returns a Json writes for a given enum [[Enum]]
   */
  def writes[A](enum: Enum[A]): Writes[A] = new Writes[A] {
    def writes(v: A): JsValue = JsString(v.toString)
  }

  /**
   * Returns a Json format for a given enum [[Enum]]
   */
  def enumFormat[A](enum: Enum[A]): Format[A] = {
    Format(reads(enum), writes(enum))
  }

}
