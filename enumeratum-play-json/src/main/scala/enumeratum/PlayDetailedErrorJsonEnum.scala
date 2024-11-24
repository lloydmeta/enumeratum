package enumeratum

import play.api.libs.json._

trait PlayDetailedErrorJsonEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val keyWrites: KeyWrites[A] = EnumFormats.keyWrites(this)

  implicit def contraKeyWrites[K <: A]: KeyWrites[K] = {
    val w = this.keyWrites

    new KeyWrites[K] {
      def writeKey(k: K) = w.writeKey(k)
    }
  }

  implicit val keyReads: KeyReads[A] = EnumFormats.keyReads(this, detailedError = true)

  implicit val jsonFormat: Format[A]               = EnumFormats.formats(this, detailedError = true)
  implicit def contraJsonWrites[B <: A]: Writes[B] = jsonFormat.contramap[B](b => b: A)
}
