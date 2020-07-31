package enumeratum

import play.api.libs.json.{Format, Writes}

trait PlayUppercaseJsonEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val jsonFormat: Format[A]               = EnumFormats.formatsUppercaseOnly(this)
  implicit def contraJsonWrites[B <: A]: Writes[B] = jsonFormat.contramap[B](b => b: A)
}
