package enumeratum

import upickle.Aliases.RW
import upickle.ReadWriter

/**
 * Enum mix-in with default Reader and Writers defined (case sensitive)
 */
trait UPickleEnum[A] { self: Enum[A] =>

  import UPickler._

  implicit val uPickleReadWriter: RW[A] = ReadWriter(writer(this).write, reader(this, false).read)

}
