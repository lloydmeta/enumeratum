package enumeratum

import doobie.util._

/**
  * Helper trait that adds implicit Doobie Get and Put for an [[Enum]]'s members
  *
  * Example:
  *
  * {{{
  * scala> import enumeratum._
  * scala> import doobie._
  * scala> import doobie.implicits._
  *
  * scala> sealed trait ShirtSize extends EnumEntry
  * scala> case object ShirtSize extends Enum[ShirtSize] with DoobieEnum[ShirtSize] {
  *      |  case object Small  extends ShirtSize
  *      |  case object Medium extends ShirtSize
  *      |  case object Large  extends ShirtSize
  *      |  val values = findValues
  *      | }
  *
  * scala> case class Shirt(size: ShirtSize)
  *
  * scala> sql"select size from Shirt".query[Shirt].to[List]
  * }}}
  */
trait DoobieEnum[A <: EnumEntry] { this: Enum[A] =>

  implicit lazy val enumMeta: Meta[A] = Doobie.meta(this)

}
