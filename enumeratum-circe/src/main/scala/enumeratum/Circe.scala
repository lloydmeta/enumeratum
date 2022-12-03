package enumeratum

import cats.syntax.either._

import io.circe.Decoder.Result
import io.circe.{Encoder, Decoder, Json, HCursor, DecodingFailure, KeyEncoder, KeyDecoder}

/** Created by Lloyd on 4/14/16.
  *
  * Copyright 2016
  */
object Circe {

  /** Returns an Encoder for the given enum
    */
  def encoder[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): Encoder[A] =
    new Encoder[A] {
      final def apply(a: A): Json = stringEncoder.apply(a.entryName)
    }

  def encoderLowercase[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): Encoder[A] =
    new Encoder[A] {
      final def apply(a: A): Json =
        stringEncoder.apply(a.entryName.toLowerCase)
    }

  def encoderUppercase[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): Encoder[A] =
    new Encoder[A] {
      final def apply(a: A): Json =
        stringEncoder.apply(a.entryName.toUpperCase)
    }

  /** Returns a Decoder for the given enum
    */
  def decoder[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): Decoder[A] =
    new Decoder[A] {
      final def apply(c: HCursor): Result[A] = stringDecoder.apply(c).flatMap { s =>
        e.withNameOption(s) match {
          case Some(member) => Right(member)
          case _ =>
            Left(DecodingFailure(s"'$s' is not a member of enum $e", c.history))
        }
      }
    }

  def decoderLowercaseOnly[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): Decoder[A] =
    new Decoder[A] {
      final def apply(c: HCursor): Result[A] = stringDecoder.apply(c).flatMap { s =>
        val maybeMember = e.withNameLowercaseOnlyOption(s)
        maybeMember match {
          case Some(member) => Right(member)
          case _ =>
            Left(DecodingFailure(s"'$s' is not a member of enum $e", c.history))
        }
      }
    }

  def decoderUppercaseOnly[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): Decoder[A] =
    new Decoder[A] {
      final def apply(c: HCursor): Result[A] = stringDecoder.apply(c).flatMap { s =>
        val maybeMember = e.withNameUppercaseOnlyOption(s)
        maybeMember match {
          case Some(member) => Right(member)
          case _ =>
            Left(DecodingFailure(s"'$s' is not a member of enum $e", c.history))
        }
      }
    }

  def decodeCaseInsensitive[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): Decoder[A] =
    new Decoder[A] {
      final def apply(c: HCursor): Result[A] = stringDecoder.apply(c).flatMap { s =>
        val maybeMember = e.withNameInsensitiveOption(s)
        maybeMember match {
          case Some(member) => Right(member)
          case _ =>
            Left(DecodingFailure(s"'$s' is not a member of enum $e", c.history))
        }
      }
    }

  /** Returns a KeyEncoder for the given enum
    */
  def keyEncoder[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): KeyEncoder[A] =
    KeyEncoder.instance(_.entryName)

  /** Returns a KeyDecoder for the given enum
    */
  def keyDecoder[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): KeyDecoder[A] =
    KeyDecoder.instance(e.withNameOption)

  private val stringEncoder = implicitly[Encoder[String]]
  private val stringDecoder = implicitly[Decoder[String]]

}
