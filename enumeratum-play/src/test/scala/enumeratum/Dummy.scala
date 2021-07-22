package enumeratum

/** Created by Lloyd on 2/4/15.
  */
sealed trait Dummy extends EnumEntry
object Dummy extends Enum[Dummy] {
  case object A extends Dummy
  case object B extends Dummy
  case object C extends Dummy
  val values = findValues
}
