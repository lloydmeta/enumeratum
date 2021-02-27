package enumeratum

import java.sql.PreparedStatement

import anorm.{Column, ToStatement}

/**
  * Provides lowercase instances for Anorm typeclasses:
  *
  * - [[anorm.Column]]
  */
trait AnormLowercaseEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit lazy val column: Column[A] =
    AnormColumn.lowercaseOnlyColumn[A](self)

  implicit lazy val toStatement = new ToStatement[A] {
    def set(s: PreparedStatement, i: Int, v: A) =
      s.setString(i, v.entryName.toLowerCase)
  }
}
