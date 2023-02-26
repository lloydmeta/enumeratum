package enumeratum.values

import scalikejdbc.{ParameterBinderFactory, ParameterBinderWithValue, TypeBinder}

import java.sql.PreparedStatement

trait ShortScalikeJDBCEnum[A <: ShortEnumEntry] extends ShortEnum[A] {
  implicit val typeBinder: TypeBinder[A] = {
    TypeBinder.short.map(withValue)
  }

  implicit val optionalTypeBinder: TypeBinder[Option[A]] = {
    TypeBinder.short.map(withValueOpt)
  }

  implicit val parameterBinderFactory: ParameterBinderFactory[A] = (entry: A) =>
    new ParameterBinderWithValue() {
      override def value: A = entry
      override def apply(stmt: PreparedStatement, idx: Int): Unit = {
        stmt.setShort(idx, value.value)
      }
    }
}
