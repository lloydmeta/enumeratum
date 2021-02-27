package enumeratum

import java.sql.PreparedStatement

import anorm.{Column, ToStatement}

/**
  * Provides insensitive instances for Anorm typeclasses:
  *
  * - [[anorm.Column]]
  * - [[anorm.ToStatement]]
  */
trait AnormInsensitiveEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val column: Column[A] =
    AnormColumn.column[A](self, insensitive = true)

  implicit val toStatement = new ToStatement[A] {
    def set(s: PreparedStatement, i: Int, v: A) =
      s.setString(i, v.entryName)
  }
}
