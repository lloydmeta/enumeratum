package enumeratum.postgres.values

import doobie._
import doobie.postgres.implicits.pgEnumStringOpt
import enumeratum.values.{StringEnumEntry, ValueEnum}

import scala.reflect.runtime.universe.TypeTag

object DoobiePgValueEnum {
  def meta[A <: StringEnumEntry: TypeTag](name: String, enum: ValueEnum[String, A]): Meta[A] = {
    pgEnumStringOpt(name, enum.withValueOpt, _.value)
  }
}
