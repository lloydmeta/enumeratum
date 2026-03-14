package enumeratum

import zio.json.{JsonDecoder, JsonEncoder, JsonFieldDecoder, JsonFieldEncoder}

object ZioJson {

  /** Returns a JsonEncoder for the given enum
    */
  def encoder[A <: EnumEntry](e: Enum[A]): JsonEncoder[A] =
    stringEncoder.contramap(_.entryName)

  def encoderLowercase[A <: EnumEntry](e: Enum[A]): JsonEncoder[A] =
    stringEncoder.contramap(_.entryName.toLowerCase)

  def encoderUppercase[A <: EnumEntry](e: Enum[A]): JsonEncoder[A] =
    stringEncoder.contramap(_.entryName.toUpperCase)

  /** Returns a JsonDecoder for the given enum
    */
  def decoder[A <: EnumEntry](e: Enum[A]): JsonDecoder[A] =
    stringDecoder.mapOrFail { s =>
      e.withNameOption(s) match {
        case Some(member) => Right(member)
        case _            => Left(s"'$s' is not a member of enum $e")
      }
    }

  def decoderLowercaseOnly[A <: EnumEntry](e: Enum[A]): JsonDecoder[A] =
    stringDecoder.mapOrFail { s =>
      e.withNameLowercaseOnlyOption(s) match {
        case Some(member) => Right(member)
        case _            => Left(s"'$s' is not a member of enum $e")
      }
    }

  def decoderUppercaseOnly[A <: EnumEntry](e: Enum[A]): JsonDecoder[A] =
    stringDecoder.mapOrFail { s =>
      e.withNameUppercaseOnlyOption(s) match {
        case Some(member) => Right(member)
        case _            => Left(s"'$s' is not a member of enum $e")
      }
    }

  def decoderCaseInsensitive[A <: EnumEntry](e: Enum[A]): JsonDecoder[A] =
    stringDecoder.mapOrFail { s =>
      e.withNameInsensitiveOption(s) match {
        case Some(member) => Right(member)
        case _            => Left(s"'$s' is not a member of enum $e")
      }
    }

  /** Returns a JsonFieldEncoder for the given enum
    */
  def keyEncoder[A <: EnumEntry](e: Enum[A]): JsonFieldEncoder[A] =
    stringFieldEncoder.contramap(_.entryName)

  /** Returns a JsonFieldDecoder for the given enum
    */
  def keyDecoder[A <: EnumEntry](e: Enum[A]): JsonFieldDecoder[A] =
    stringFieldDecoder.mapOrFail { s =>
      e.withNameOption(s) match {
        case Some(member) => Right(member)
        case _            => Left(s"'$s' is not a member of enum $e")
      }
    }

  private val stringEncoder      = implicitly[JsonEncoder[String]]
  private val stringDecoder      = implicitly[JsonDecoder[String]]
  private val stringFieldEncoder = implicitly[JsonFieldEncoder[String]]
  private val stringFieldDecoder = implicitly[JsonFieldDecoder[String]]

}
