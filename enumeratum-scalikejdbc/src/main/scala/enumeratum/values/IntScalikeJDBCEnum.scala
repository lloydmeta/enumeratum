package enumeratum.values

import scalikejdbc.{ParameterBinderFactory, ParameterBinderWithValue, TypeBinder}

import java.sql.PreparedStatement

trait IntScalikeJDBCEnum[A <: IntEnumEntry] extends IntEnum[A] {
  implicit val typeBinder: TypeBinder[A] = {
    TypeBinder.int.map(withValue)
  }

  implicit val optionalTypeBinder: TypeBinder[Option[A]] = {
    TypeBinder.int.map(withValueOpt)
  }

  implicit val parameterBinderFactory: ParameterBinderFactory[A] = (entry: A) =>
    new ParameterBinderWithValue() {
      override def value: A = entry
      override def apply(stmt: PreparedStatement, idx: Int): Unit = {
        stmt.setInt(idx, value.value)
      }
    }
}
