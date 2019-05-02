package enumeratum

import doobie.util._

object Doobie {

  /**
    * Returns an Encoder for the given enum
    */
  def meta[A <: EnumEntry](enum: Enum[A]): Meta[A] =
    Meta[String].imap(enum.withName)(_.entryName)
}
