package enumeratum

import io.getquill.MappedEncoding

object Quill {

  /**
    * Returns an Encoder for the given enum
    */
  def encoder[A <: EnumEntry](enum: Enum[A]): MappedEncoding[A, String] =
    MappedEncoding(_.entryName)

  /**
    * Returns a Decoder for the given enum
    */
  def decoder[A <: EnumEntry](enum: Enum[A]): MappedEncoding[String, A] =
    MappedEncoding(enum.withName)
}
