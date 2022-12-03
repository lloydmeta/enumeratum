package enumeratum.values

import cats.syntax.either._

import io.circe.Decoder.Result
import io.circe._

/** Created by Lloyd on 4/14/16.
  *
  * Copyright 2016
  */
object Circe {

  /** Returns an Encoder for the provided ValueEnum
    */
  def encoder[ValueType: Encoder, EntryType <: ValueEnumEntry[ValueType]](
      @deprecatedName(Symbol("enum")) e: ValueEnum[ValueType, EntryType]
  ): Encoder[EntryType] = {
    new Encoder[EntryType] {
      private val valueEncoder = implicitly[Encoder[ValueType]]

      def apply(a: EntryType): Json = valueEncoder(a.value)
    }
  }

  /** Returns a Decoder for the provided ValueEnum
    */
  def decoder[ValueType: Decoder, EntryType <: ValueEnumEntry[ValueType]](
      @deprecatedName(Symbol("enum")) e: ValueEnum[ValueType, EntryType]
  ): Decoder[EntryType] = {
    new Decoder[EntryType] {
      private val valueDecoder = implicitly[Decoder[ValueType]]

      def apply(c: HCursor): Result[EntryType] =
        valueDecoder(c).flatMap { v =>
          e.withValueOpt(v) match {
            case Some(member) => Right(member)
            case _ =>
              Left(DecodingFailure(s"$v is not a member of enum $e", c.history))
          }
        }
    }
  }

  def keyEncoder[EntryType <: ValueEnumEntry[String]](
      @deprecatedName(Symbol("enum")) e: ValueEnum[String, EntryType]
  ): KeyEncoder[EntryType] = KeyEncoder.instance(_.value)

  def keyDecoder[EntryType <: ValueEnumEntry[String]](
      @deprecatedName(Symbol("enum")) e: ValueEnum[String, EntryType]
  ): KeyDecoder[EntryType] = KeyDecoder.instance(e.withValueOpt)

}
