package enumeratum

import play.api.libs.json.Format

trait PlayJsonEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val jsonFormat: Format[A] = EnumFormats.formats(this)
}
