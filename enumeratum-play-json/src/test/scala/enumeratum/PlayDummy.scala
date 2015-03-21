package enumeratum

/**
 * Created by Lloyd on 2/4/15.
 */
sealed trait PlayJsonDummy

object PlayJsonDummy extends PlayEnum[PlayJsonDummy] {
  case object A extends PlayJsonDummy
  case object B extends PlayJsonDummy
  case object C extends PlayJsonDummy
  val values = findValues
}