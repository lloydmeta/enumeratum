package enumeratum

import play.api.libs.json.{Format, Writes}

trait PlayJsonEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val jsonFormat: Format[A]               = EnumFormats.formats(this)
  implicit def contraJsonWrites[B <: A]: Writes[B] = jsonFormat.contramap[B](b => b: A)
}
