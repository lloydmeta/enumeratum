package enumeratum.values

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */

sealed abstract class PlayDrinks(val value: Short, name: String) extends ShortEnumEntry

case object PlayDrinks extends ShortEnum[PlayDrinks] with PlayJsonShortValeEnum[PlayDrinks] {

  case object OrangeJuice extends PlayDrinks(value = 1, name = "oj")
  case object AppleJuice extends PlayDrinks(value = 2, name = "aj")
  case object Cola extends PlayDrinks(value = 3, name = "cola")
  case object Beer extends PlayDrinks(value = 4, name = "beer")

  val values = findValues

}