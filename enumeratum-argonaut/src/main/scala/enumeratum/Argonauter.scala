package enumeratum

import argonaut._
import Argonaut._

/**
 * Created by alonsodomin on 14/10/2016.
 */
object Argonauter {

  private def encoder0[A <: EnumEntry](f: A => String): EncodeJson[A] =
    stringEncoder.contramap(f)

  def encoder[A <: EnumEntry](enum: Enum[A]): EncodeJson[A] =
    encoder0[A](_.entryName)

  def encoderLowercase[A <: EnumEntry](enum: Enum[A]): EncodeJson[A] =
    encoder0[A](_.entryName.toLowerCase)

  def encoderUppercase[A <: EnumEntry](enum: Enum[A]): EncodeJson[A] =
    encoder0[A](_.entryName.toUpperCase)

  private def decoder0[A <: EnumEntry](enum: Enum[A])(f: String => Option[A]): DecodeJson[A] =
    DecodeJson { cursor =>
      stringDecoder(cursor).flatMap { enumStr =>
        f(enumStr) match {
          case Some(a) => okResult(a)
          case _ => failResult(s"$enumStr' is not a member of enum $enum", cursor.history)
        }
      }
    }

  def decoder[A <: EnumEntry](enum: Enum[A]): DecodeJson[A] =
    decoder0(enum)(enum.withNameOption)

  def decoderLowercaseOnly[A <: EnumEntry](enum: Enum[A]): DecodeJson[A] =
    decoder0(enum)(enum.withNameLowercaseOnlyOption)

  def decoderUppercaseOnly[A <: EnumEntry](enum: Enum[A]): DecodeJson[A] =
    decoder0(enum)(enum.withNameUppercaseOnlyOption)

  private val stringEncoder = implicitly[EncodeJson[String]]
  private val stringDecoder = implicitly[DecodeJson[String]]

}
