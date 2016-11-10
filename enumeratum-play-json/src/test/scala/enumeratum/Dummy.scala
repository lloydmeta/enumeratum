package enumeratum

/**
 * Created by Lloyd on 2/4/15.
 */
sealed trait Dummy extends EnumEntry
object Dummy extends Enum[Dummy] with PlayJsonEnum[Dummy] {
  case object A extends Dummy
  case object B extends Dummy
  case object c extends Dummy
  val values = findValues
}
