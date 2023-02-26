package enumeratum.values

import scalikejdbc.{ParameterBinderFactory, ParameterBinderWithValue, TypeBinder}

import java.sql.PreparedStatement

trait StringScalikeJDBCEnum[A <: StringEnumEntry] extends StringEnum[A] {
  implicit val typeBinder: TypeBinder[A] = {
    TypeBinder.string.map(withValue)
  }

  implicit val optionalTypeBinder: TypeBinder[Option[A]] = {
    TypeBinder.string.map(withValueOpt)
  }

  implicit val parameterBinderFactory: ParameterBinderFactory[A] = (entry: A) =>
    new ParameterBinderWithValue() {
      override def value: A = entry
      override def apply(stmt: PreparedStatement, idx: Int): Unit = {
        stmt.setString(idx, value.value)
      }
    }
}
