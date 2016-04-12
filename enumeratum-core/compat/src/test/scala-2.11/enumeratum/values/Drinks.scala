package enumeratum.values

/**
 * Created by Lloyd on 4/12/16.
 *
 * Copyright 2016
 */
sealed abstract class Drinks(val value: Short, name: String) extends ShortEnumEntry

case object Drinks extends ShortEnum[Drinks] {

  case object OrangeJuice extends Drinks(value = 1, name = "oj")
  case object AppleJuice extends Drinks(value = 2, name = "aj")
  case object Cola extends Drinks(value = 3, name = "cola")
  case object Beer extends Drinks(value = 4, name = "beer")

  val values = findValues

}
