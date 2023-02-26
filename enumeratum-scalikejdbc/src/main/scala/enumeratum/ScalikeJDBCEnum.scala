package enumeratum

import scalikejdbc.{ParameterBinderFactory, ParameterBinderWithValue, TypeBinder}

import java.sql.PreparedStatement

trait ScalikeJDBCEnum[A <: EnumEntry] extends Enum[A] {
  implicit val typeBinder: TypeBinder[A] = {
    TypeBinder.string.map(withName)
  }

  implicit val optionalTypeBinder: TypeBinder[Option[A]] = {
    TypeBinder.string.map(withNameOption)
  }

  implicit val parameterBinderFactory: ParameterBinderFactory[A] = (entry: A) =>
    new ParameterBinderWithValue() {
      override def value: A = entry
      override def apply(stmt: PreparedStatement, idx: Int): Unit = {
        stmt.setString(idx, value.entryName)
      }
    }
}
