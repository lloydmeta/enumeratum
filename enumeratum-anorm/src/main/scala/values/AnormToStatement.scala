package enumeratum.values

import java.sql.PreparedStatement

import anorm.ToStatement

private[values] object AnormToStatement {
  def apply[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]
  )(
      implicit baseToStmt: ToStatement[ValueType]
  ): ToStatement[EntryType] = new ToStatement[EntryType] {
    def set(s: PreparedStatement, i: Int, e: EntryType) =
      baseToStmt.set(s, i, e.value)
  }
}
