package enumeratum.values

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */
sealed abstract class JsonDrinks(val value: Short, name: String) extends ShortEnumEntry

case object JsonDrinks extends ShortEnum[JsonDrinks] with ShortPlayJsonValueEnum[JsonDrinks] {

  case object OrangeJuice extends JsonDrinks(value = 1, name = "oj")
  case object AppleJuice extends JsonDrinks(value = 2, name = "aj")
  case object Cola extends JsonDrinks(value = 3, name = "cola")
  case object Beer extends JsonDrinks(value = 4, name = "beer")

  val values = findValues

}
