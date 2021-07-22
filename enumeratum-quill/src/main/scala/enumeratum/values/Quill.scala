package enumeratum.values

import io.getquill.MappedEncoding

object Quill {

  /** Returns an Encoder for the provided ValueEnum
    */
  def encoder[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]
  ): MappedEncoding[EntryType, ValueType] = MappedEncoding(_.value)

  /** Returns a Decoder for the provided ValueEnum
    */
  def decoder[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]
  ): MappedEncoding[ValueType, EntryType] = MappedEncoding(enum.withValue)
}
