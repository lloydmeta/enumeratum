package enumeratum.values

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
   * Returns an Encoder for the provided ValueEnum
   */
  def encoder[ValueType <: AnyVal: Encoder, EntryType <: ValueEnumEntry[ValueType]](enum: ValueEnum[ValueType, EntryType]): Encoder[EntryType] = {
    new Encoder[EntryType] {
      private val valueEncoder = implicitly[Encoder[ValueType]]
      def apply(a: EntryType): Json = valueEncoder.apply(a.value)
    }
  }

  /**
   * Returns a Decoder for the provided ValueEnum
   */
  def decoder[ValueType <: AnyVal: Decoder, EntryType <: ValueEnumEntry[ValueType]](enum: ValueEnum[ValueType, EntryType]): Decoder[EntryType] = {
    new Decoder[EntryType] {
      private val valueDecoder = implicitly[Decoder[ValueType]]
      def apply(c: HCursor): Result[EntryType] = valueDecoder.apply(c).flatMap { v =>
        val maybeBound: Option[EntryType] = enum.withValueOpt(v)
        maybeBound match {
          case Some(member) => Xor.Right(member)
          case _ => Xor.Left(DecodingFailure(s"$v is not a member of enum $enum", c.history))
        }
      }
    }
  }
}
