package enumeratum

import play.api.libs.json.Format

trait PlayInsensitiveJsonEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val jsonFormat: Format[A] = EnumFormats.formats(this, insensitive = true)
}
