package enumeratum

/** @author
  *   Alessandro Lacava (@lambdista)
  * @since 2016-04-23
  */
sealed trait Dummy extends EnumEntry
object Dummy extends Enum[Dummy] with ReactiveMongoBsonEnum[Dummy] {
  case object A extends Dummy
  case object B extends Dummy
  case object c extends Dummy
  val values = findValues
}
