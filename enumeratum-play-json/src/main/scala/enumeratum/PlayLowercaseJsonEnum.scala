package enumeratum

import play.api.libs.json._

trait PlayLowercaseJsonEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val keyWrites: KeyWrites[A] = EnumFormats.keyWritesLowercaseOnly(this)

  implicit def contraKeyWrites[K <: A]: KeyWrites[K] = {
    val w = this.keyWrites

    new KeyWrites[K] {
      def writeKey(k: K) = w.writeKey(k)
    }
  }

  implicit val keyReads: KeyReads[A] = EnumFormats.keyReadsLowercaseOnly(this)

  implicit val jsonFormat: Format[A]               = EnumFormats.formatsLowerCaseOnly(this)
  implicit def contraJsonWrites[B <: A]: Writes[B] = jsonFormat.contramap[B](b => b: A)
}
