package enumeratum.values

import anorm.{Column, TypeDoesNotMatch}

private[values] object AnormColumn {
  def apply[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]
  )(
      implicit baseColumn: Column[ValueType]
  ): Column[EntryType] = Column.nonNull[EntryType] {
    case (value, meta) =>
      baseColumn(value, meta) match {
        case Left(err) =>
          Left(err)

        case Right(s) =>
          enum.withValueOpt(s) match {
            case Some(obj) => Right(obj)
            case None      => Left(TypeDoesNotMatch(s"Invalid value: $s"))
          }
      }
  }
}
