package enumeratum

import java.sql.PreparedStatement

import anorm.{Column, ToStatement}

/**
  * Provides uppercase instances for Anorm typeclasses:
  *
  * - [[anorm.Column]]
  */
trait AnormUppercaseEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit lazy val column: Column[A] =
    AnormColumn.uppercaseOnlyColumn[A](self)

  implicit lazy val toStatement = new ToStatement[A] {
    def set(s: PreparedStatement, i: Int, v: A) =
      s.setString(i, v.entryName.toUpperCase)
  }
}
