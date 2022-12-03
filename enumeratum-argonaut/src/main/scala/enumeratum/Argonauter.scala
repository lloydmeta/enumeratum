package enumeratum

import argonaut._, Argonaut._

/** Created by alonsodomin on 14/10/2016.
  */
object Argonauter {

  private def encoder0[A <: EnumEntry](f: A => String): EncodeJson[A] =
    stringEncoder.contramap(f)

  def encoder[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): EncodeJson[A] =
    encoder0[A](_.entryName)

  def encoderLowercase[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): EncodeJson[A] =
    encoder0[A](_.entryName.toLowerCase)

  def encoderUppercase[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): EncodeJson[A] =
    encoder0[A](_.entryName.toUpperCase)

  private def decoder0[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  )(f: String => Option[A]): DecodeJson[A] =
    DecodeJson { cursor =>
      stringDecoder(cursor).flatMap { enumStr =>
        f(enumStr) match {
          case Some(a) => okResult(a)
          case _       => failResult(s"'$enumStr' is not a member of enum $e", cursor.history)
        }
      }
    }

  def decoder[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): DecodeJson[A] =
    decoder0(e)(e.withNameOption)

  def decoderLowercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): DecodeJson[A] =
    decoder0(e)(e.withNameLowercaseOnlyOption)

  def decoderUppercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): DecodeJson[A] =
    decoder0(e)(e.withNameUppercaseOnlyOption)

  private val stringEncoder = implicitly[EncodeJson[String]]
  private val stringDecoder = implicitly[DecodeJson[String]]

}
