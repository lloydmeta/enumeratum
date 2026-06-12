package enumeratum

import org.typelevel.doobie.util._
import org.typelevel.doobie.Meta

/** Helper trait that adds implicit Doobie Get and Put for an [[Enum]] 's members
  *
  * Example:
  *
  * {{{
  * scala> import enumeratum._
  * scala> import org.typelevel.doobie._
  * scala> import org.typelevel.doobie.implicits._
  *
  * scala> sealed trait ShirtSize extends EnumEntry
  * scala> case object ShirtSize extends Enum[ShirtSize] with DoobieEnum[ShirtSize] {
  *     |  case object Small  extends ShirtSize
  *     |  case object Medium extends ShirtSize
  *     |  case object Large  extends ShirtSize
  *     |  val values = findValues
  *     | }
  *
  * scala> case class Shirt(size: ShirtSize)
  *
  * scala> sql"select size from Shirt".query[Shirt].to[List]
  * }}}
  */
trait DoobieEnum[A <: EnumEntry] { this: Enum[A] =>

  implicit def enumSingletonPut[B <: A with Singleton]: Put[B] = Put[A].contramap[B](identity)

  implicit lazy val enumMeta: Meta[A] = Doobie.meta(this)

}
