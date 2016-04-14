package enumeratum

import cats.data.Xor
import io.circe.Decoder.Result
import io.circe._

/**
 * Created by Lloyd on 4/14/16.
 *
 * Copyright 2016
 */
object Circe {

  /**
   * Returns an Encoder for the given enum
   */
  def encoder[A <: EnumEntry](enum: Enum[A]): Encoder[A] = new Encoder[A] {
    final def apply(a: A): Json = stringEncoder.apply(a.entryName)
  }

  /**
   * Returns a Decoder for the given enum
   */
  def decoder[A <: EnumEntry](enum: Enum[A]): Decoder[A] = new Decoder[A] {
    final def apply(c: HCursor): Result[A] = stringDecoder.apply(c).flatMap { s =>
      val maybeMember = enum.withNameOption(s)
      maybeMember match {
        case Some(member) => Xor.right(member)
        case None => Xor.left(DecodingFailure(s"$s' is not a member of enum $enum", c.history))
      }
    }
  }

  private val stringEncoder = implicitly[Encoder[String]]
  private val stringDecoder = implicitly[Decoder[String]]

}