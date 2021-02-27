package enumeratum

import anorm.Column

/**
  * Provides uppercase instances for Anorm typeclasses:
  *
  * - [[anorm.Column]]
  */
trait AnormUppercaseEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit def column: Column[A] =
    AnormColumn.uppercaseOnlyColumn[A](self)
}
