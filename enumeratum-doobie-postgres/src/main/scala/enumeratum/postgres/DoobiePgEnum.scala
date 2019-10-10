package enumeratum.postgres

import doobie._
import doobie.postgres.implicits.pgEnumStringOpt
import enumeratum._

import scala.reflect.runtime.universe.TypeTag

object DoobiePgEnum {

  /**
    * {{{
    *   trait Foo extends EnumEntry
    *   object Foo extends Enum[Foo] {
    *     case object Bar extends Foo
    *
    *     val values = findValues
    *     implicit val doobieMeta: Meta[Foo] = DoobiePgEnum.meta("foo", Foo)
    *   }
    * }}}
    * @param name type in postgres
    * @return Meta[A] for the given enum
    */
  def meta[A <: EnumEntry: TypeTag](name: String, enum: Enum[A]): Meta[A] = {
    pgEnumStringOpt(name, enum.withNameOption, _.entryName)
  }
}
