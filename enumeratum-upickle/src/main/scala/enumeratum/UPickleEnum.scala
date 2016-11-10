package enumeratum

import upickle.default.Aliases.RW
import upickle.default.ReadWriter

/**
 * Enum mix-in with default Reader and Writers defined (case sensitive)
 */
trait UPickleEnum[A <: EnumEntry] { self: Enum[A] =>

  import UPickler._

  implicit val uPickleReadWriter: RW[A] =
    ReadWriter(writer(this).write, reader(enum = this, insensitive = false).read)

}
