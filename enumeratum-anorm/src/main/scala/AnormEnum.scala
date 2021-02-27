package enumeratum

import anorm.Column

/**
  * Provides instances for Anorm typeclasses:
  *
  * - [[anorm.Column]]
  */
trait AnormEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit def column: Column[A] =
    AnormColumn.column[A](self, insensitive = false)
}
