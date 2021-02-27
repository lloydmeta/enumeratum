package enumeratum

import java.sql.PreparedStatement

import anorm.{Column, ToStatement}

/**
  * Provides instances for Anorm typeclasses:
  *
  * - [[anorm.Column]]
  */
trait AnormEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit lazy val column: Column[A] =
    AnormColumn.column[A](self, insensitive = false)

  implicit lazy val toStatement: ToStatement[A] = new ToStatement[A] {
    def set(s: PreparedStatement, i: Int, v: A) = s.setString(i, v.entryName)
  }
}
