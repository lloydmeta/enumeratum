package enumeratum
import io.circe.{KeyEncoder, KeyDecoder}

/** Helper trait that adds implicit Circe KeyEncoder/KeyDecoder for an [[Enum]] 's members.
  */
trait CirceKeyEnum[A <: EnumEntry] { this: Enum[A] =>
  implicit val circeKeyEncoder: KeyEncoder[A] = Circe.keyEncoder(this)
  implicit val circeKeyDecoder: KeyDecoder[A] = Circe.keyDecoder(this)
}
