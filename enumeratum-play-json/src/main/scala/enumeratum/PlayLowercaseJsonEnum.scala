package enumeratum

import play.api.libs.json.{Format, Writes}

trait PlayLowercaseJsonEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val jsonFormat: Format[A]               = EnumFormats.formatsLowerCaseOnly(this)
  implicit def contraJsonWrites[B <: A]: Writes[B] = jsonFormat.contramap[B](b => b: A)
}
