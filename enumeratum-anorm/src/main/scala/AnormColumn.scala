package enumeratum

import anorm.{Column, TypeDoesNotMatch}

private[enumeratum] object AnormColumn {
  def column[A <: EnumEntry](enum: Enum[A], insensitive: Boolean): Column[A] =
    if (insensitive) {
      parse[A](enum)(enum.withNameInsensitiveOption)
    } else {
      parse[A](enum)(enum.withNameOption)
    }

  def lowercaseOnlyColumn[A <: EnumEntry](enum: Enum[A]): Column[A] =
    parse[A](enum)(enum.withNameLowercaseOnlyOption)

  def uppercaseOnlyColumn[A <: EnumEntry](enum: Enum[A]): Column[A] =
    parse[A](enum)(enum.withNameUppercaseOnlyOption)

  // ---

  private def parse[A <: EnumEntry](enum: Enum[A])(extract: String => Option[A]): Column[A] =
    Column.nonNull[A] {
      case (s: String, _) =>
        extract(s) match {
          case Some(result) => Right(result)
          case None         => Left(TypeDoesNotMatch(s"Invalid value: $s"))
        }

      case (_, meta) =>
        Left(
          TypeDoesNotMatch(
            s"Column '${meta.column.qualified}' expected to be String; Found ${meta.clazz}"))
    }

}
