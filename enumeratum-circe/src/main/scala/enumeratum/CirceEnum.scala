package enumeratum
import io.circe.{ Decoder, Encoder }

/**
 * Created by Lloyd on 4/14/16.
 *
 * Copyright 2016
 */

/**
 * Helper trait that adds implicit Circe encoders and decoders for an [[Enum]]'s members
 */
trait CirceEnum[A <: EnumEntry] { this: Enum[A] =>

  implicit val circeEncoder: Encoder[A] = Circe.encoder(this)

  implicit val circeDecoder: Decoder[A] = Circe.decoder(this)

}
