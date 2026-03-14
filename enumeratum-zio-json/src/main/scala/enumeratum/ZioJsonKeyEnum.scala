package enumeratum

import zio.json.{JsonFieldDecoder, JsonFieldEncoder}

/** Helper trait that adds implicit zio-json JsonFieldEncoder/JsonFieldDecoder for an [[Enum]]'s
  * members, allowing them to be used as JSON object keys.
  */
trait ZioJsonKeyEnum[A <: EnumEntry] { this: Enum[A] =>
  implicit val zioJsonKeyEncoder: JsonFieldEncoder[A] = ZioJson.keyEncoder(this)
  implicit val zioJsonKeyDecoder: JsonFieldDecoder[A] = ZioJson.keyDecoder(this)
}
