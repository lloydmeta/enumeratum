package enumeratum

import anorm.Column

/**
  * Provides lowercase instances for Anorm typeclasses:
  *
  * - [[anorm.Column]]
  */
trait AnormLowercaseEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit def column: Column[A] =
    AnormColumn.lowercaseOnlyColumn[A](self)
}
