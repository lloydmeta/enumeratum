package enumeratum.values

import scalikejdbc.{ParameterBinderFactory, ParameterBinderWithValue, TypeBinder}

import java.sql.PreparedStatement

trait CharScalikeJDBCEnum[A <: CharEnumEntry] extends CharEnum[A] {
  implicit val typeBinder: TypeBinder[A] = {
    TypeBinder.string.map { x => withValue(x.charAt(0)) }
  }

  implicit val optionalTypeBinder: TypeBinder[Option[A]] = {
    TypeBinder.string.map(_.headOption.map(withValue))
  }

  implicit val parameterBinderFactory: ParameterBinderFactory[A] = (entry: A) =>
    new ParameterBinderWithValue() {
      override def value: A = entry
      override def apply(stmt: PreparedStatement, idx: Int): Unit = {
        stmt.setString(idx, value.value.toString)
      }
    }
}
