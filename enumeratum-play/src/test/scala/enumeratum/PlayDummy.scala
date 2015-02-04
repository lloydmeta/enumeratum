package enumeratum

/**
 * Created by Lloyd on 2/4/15.
 */
sealed trait PlayDummy

object PlayDummy extends PlayEnum[PlayDummy] {
  case object A extends PlayDummy
  case object B extends PlayDummy
  case object C extends PlayDummy
  val values = findValues
}