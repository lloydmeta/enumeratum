package enumeratum

import zio.json.{JsonDecoder, JsonEncoder}

/** Helper trait that adds implicit zio-json encoders and decoders for an [[Enum]]'s members
  *
  * Example:
  *
  * {{{
  * scala> import enumeratum._
  * scala> import zio.json._
  *
  * scala> sealed trait ShirtSize extends EnumEntry
  * scala> case object ShirtSize extends Enum[ShirtSize] with ZioJsonEnum[ShirtSize] {
  *     |  case object Small  extends ShirtSize
  *     |  case object Medium extends ShirtSize
  *     |  case object Large  extends ShirtSize
  *     |  val values = findValues
  *     | }
  *
  * scala> val size: ShirtSize = ShirtSize.Small
  *
  * scala> size.toJson
  * res0: String = "Small"
  *
  * scala> """"Large"""".fromJson[ShirtSize]
  * res1: Either[String, ShirtSize] = Right(Large)
  *
  * scala> """"XLarge"""".fromJson[ShirtSize]
  * res2: Either[String, ShirtSize] = Left('XLarge' is not a member of enum ShirtSize)
  * }}}
  */
trait ZioJsonEnum[A <: EnumEntry] { this: Enum[A] =>

  /** Implicit JsonEncoder for this enum
    */
  implicit val zioJsonEncoder: JsonEncoder[A] = ZioJson.encoder(this)

  /** Implicit JsonDecoder for this enum
    */
  implicit val zioJsonDecoder: JsonDecoder[A] = ZioJson.decoder(this)
}
