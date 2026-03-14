package enumeratum.values

import zio.json.{JsonDecoder, JsonEncoder, JsonFieldDecoder, JsonFieldEncoder}

object ZioJson {

  /** Returns a JsonEncoder for the provided ValueEnum
    */
  def encoder[ValueType: JsonEncoder, EntryType <: ValueEnumEntry[ValueType]](
      e: ValueEnum[ValueType, EntryType]
  ): JsonEncoder[EntryType] =
    JsonEncoder[ValueType].contramap(_.value)

  /** Returns a JsonDecoder for the provided ValueEnum
    */
  def decoder[ValueType: JsonDecoder, EntryType <: ValueEnumEntry[ValueType]](
      e: ValueEnum[ValueType, EntryType]
  ): JsonDecoder[EntryType] =
    JsonDecoder[ValueType].mapOrFail { v =>
      e.withValueOpt(v) match {
        case Some(member) => Right(member)
        case _            => Left(s"$v is not a member of enum $e")
      }
    }

  def keyEncoder[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      e: ValueEnum[ValueType, EntryType]
  )(implicit fe: JsonFieldEncoder[ValueType]): JsonFieldEncoder[EntryType] =
    fe.contramap(_.value)

  def keyDecoder[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      e: ValueEnum[ValueType, EntryType]
  )(implicit fd: JsonFieldDecoder[ValueType]): JsonFieldDecoder[EntryType] =
    fd.mapOrFail { v =>
      e.withValueOpt(v) match {
        case Some(member) => Right(member)
        case _            => Left(s"$v is not a member of enum $e")
      }
    }

}
