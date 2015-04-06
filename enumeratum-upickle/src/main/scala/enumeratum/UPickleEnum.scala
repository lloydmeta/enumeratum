package enumeratum

import upickle.{ Js, Writer, Reader }

/**
 * Enum mix-in with default Reader and Writers defined (case sensitive)
 */
trait UPickleEnum[A] { self: Enum[A] =>

  implicit val upickleEnumWriter: Writer[A] = UPickler.writer(this)

  implicit val upickEnumReader: Reader[A] = UPickler.reader(this, false)
}
