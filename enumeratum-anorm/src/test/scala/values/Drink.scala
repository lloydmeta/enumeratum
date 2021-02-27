package enumeratum.values

sealed abstract class Drink(val value: Short, name: String) extends ShortEnumEntry

case object Drink extends ShortEnum[Drink] with ShortAnormValueEnum[Drink] {
  case object OrangeJuice extends Drink(value = 1, name = "oj")
  case object AppleJuice  extends Drink(value = 2, name = "aj")
  case object Cola        extends Drink(value = 3, name = "cola")
  case object Beer        extends Drink(value = 4, name = "beer")

  val values = findValues
}
