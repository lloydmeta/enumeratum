package enumeratum.values

import scalikejdbc.{ParameterBinderFactory, ParameterBinderWithValue, TypeBinder}

import java.sql.PreparedStatement

trait ByteScalikeJDBCEnum[A <: ByteEnumEntry] extends ByteEnum[A] {
  implicit val typeBinder: TypeBinder[A] = {
    TypeBinder.byte.map(withValue)
  }

  implicit val optionalTypeBinder: TypeBinder[Option[A]] = {
    TypeBinder.byte.map(withValueOpt)
  }

  implicit val parameterBinderFactory: ParameterBinderFactory[A] = (entry: A) =>
    new ParameterBinderWithValue() {
      override def value: A = entry
      override def apply(stmt: PreparedStatement, idx: Int): Unit = {
        stmt.setByte(idx, value.value)
      }
    }
}
