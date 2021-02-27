package enumeratum

import java.sql.PreparedStatement

import anorm.{Column, ToStatement}

/**
  * Provides insensitive instances for Anorm typeclasses:
  *
  * - [[anorm.Column]]
  */
trait AnormInsensitiveEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit lazy val column: Column[A] =
    AnormColumn.column[A](self, insensitive = true)

  implicit lazy val toStatement = new ToStatement[A] {
    def set(s: PreparedStatement, i: Int, v: A) =
      s.setString(i, v.entryName)
  }
}
