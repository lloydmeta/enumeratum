package enumeratum

import play.api.libs.json.Format

trait PlayUppercaseJsonEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val jsonFormat: Format[A] = EnumFormats.formatsUppercaseOnly(this)
}
