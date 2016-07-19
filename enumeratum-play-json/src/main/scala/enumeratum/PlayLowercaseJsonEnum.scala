package enumeratum

import play.api.libs.json.Format

trait PlayLowercaseJsonEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val jsonFormat: Format[A] = EnumFormats.formatsLowerCaseOnly(this)
}
