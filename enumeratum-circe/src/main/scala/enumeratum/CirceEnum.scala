package enumeratum
import io.circe.{Decoder, Encoder}

/**
  * Created by Lloyd on 4/14/16.
  *
  * Copyright 2016
  */
/**
  * Helper trait that adds implicit Circe encoders and decoders for an [[Enum]]'s members
  *
  * Example:
  *
  * {{{
  * scala> import enumeratum._
  * scala> import cats.syntax.either._
  * scala> import io.circe._
  * scala> import io.circe.syntax._
  *
  * scala> sealed trait ShirtSize extends EnumEntry
  * scala> case object ShirtSize extends Enum[ShirtSize] with CirceEnum[ShirtSize] {
  *      |  case object Small  extends ShirtSize
  *      |  case object Medium extends ShirtSize
  *      |  case object Large  extends ShirtSize
  *      |  val values = findValues
  *      | }
  *
  * scala> val size: ShirtSize = ShirtSize.Small
  *
  * scala> size.asJson
  * res0: Json = "Small"
  *
  * scala> Json.fromString("Large").as[ShirtSize]
  * res1: Decoder.Result[ShirtSize] = Right(Large)
  *
  *
  * scala> Json.fromString("XLarge").as[ShirtSize]
  * res2: Decoder.Result[ShirtSize] = Left(DecodingFailure('XLarge' is not a member of enum ShirtSize, List()))
  * }}}
  */
trait CirceEnum[A <: EnumEntry] { this: Enum[A] =>

  /**
    * Implicit Encoder for this enum
    */
  implicit val circeEncoder: Encoder[A] = Circe.encoder(this)

  /**
    * Implicit Decoder for this enum
    */
  implicit val circeDecoder: Decoder[A] = Circe.decoder(this)

}
