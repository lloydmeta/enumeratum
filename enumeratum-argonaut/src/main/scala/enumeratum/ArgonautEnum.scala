package enumeratum

import argonaut._

/**
  * Created by alonsodomin on 14/10/2016.
  */
/**
  * Trait that automatically adds Argonaut Json Encoders and Decoders typeclasses to your
  * Enumeratum enums.
  *
  * Example:
  *
  * {{{
  *  scala> import enumeratum._
  *  scala> import argonaut._
  *  scala> import Argonaut._
  *
  *  scala> sealed trait TrafficLight extends EnumEntry
  *  scala> case object TrafficLight extends Enum[TrafficLight] with ArgonautEnum[TrafficLight] {
  *       |   case object Red    extends TrafficLight
  *       |   case object Yellow extends TrafficLight
  *       |   case object Green  extends TrafficLight
  *       |   val values = findValues
  *       | }
  *
  *  scala> val light: TrafficLight = TrafficLight.Red
  *  scala> light.asJson
  *  res0: Json = "Red"
  *
  *  scala> Json.jString("Red").as[TrafficLight]
  *  res1: DecodeResult[TrafficLight] = DecodeResult(Right(Red))
  *
  *  scala> Json.jString("Purple").as[TrafficLight]
  *  res1: DecodeResult[TrafficLight] = DecodeResult(Left((Purple' is not a member of enum TrafficLight,CursorHistory(List()))))
  * }}}
  * @tparam A
  */
trait ArgonautEnum[A <: EnumEntry] { this: Enum[A] =>

  implicit val argonautEncoder: EncodeJson[A] = Argonauter.encoder(this)

  implicit val argonautDecoder: DecodeJson[A] = Argonauter.decoder(this)

}
