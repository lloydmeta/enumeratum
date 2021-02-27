package enumeratum

import java.sql.PreparedStatement

import anorm.{Column, ToStatement}

/**
  * Provides lowercase instances for Anorm typeclasses:
  *
  * - [[anorm.Column]]
  * - [[anorm.ToStatement]]
  */
trait AnormLowercaseEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val column: Column[A] =
    AnormColumn.lowercaseOnlyColumn[A](self)

  implicit val toStatement = new ToStatement[A] {
    def set(s: PreparedStatement, i: Int, v: A) =
      s.setString(i, v.entryName.toLowerCase)
  }
}
