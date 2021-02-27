package enumeratum

import anorm.Column

/**
  * Provides insensitive instances for Anorm typeclasses:
  *
  * - [[anorm.Column]]
  */
trait AnormInsensitiveEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit def column: Column[A] =
    AnormColumn.column[A](self, insensitive = true)
}
