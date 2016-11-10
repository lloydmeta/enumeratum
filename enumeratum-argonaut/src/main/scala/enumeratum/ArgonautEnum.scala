package enumeratum

import argonaut._

/**
  * Created by alonsodomin on 14/10/2016.
  */
trait ArgonautEnum[A <: EnumEntry] { this: Enum[A] =>

  implicit val argonautEncoder: EncodeJson[A] = Argonauter.encoder(this)

  implicit val argonautDecoder: DecodeJson[A] = Argonauter.decoder(this)

}
