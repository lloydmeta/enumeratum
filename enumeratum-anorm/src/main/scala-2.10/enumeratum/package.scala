import anorm.{Column, MetaDataItem, SqlRequestError, MayErr}

package object enumeratum {
  implicit class ColumnOps[A](val column: Column[A]) extends AnyVal {
    final def mapResult[B](f: A => Either[SqlRequestError, B]): Column[B] =
      Column[B] { (v: Any, m: MetaDataItem) =>
        column(v, m).flatMap(x => MayErr(f(x)))
      }
  }
}
