package enumeratum.values

/**
 * Created by Lloyd on 9/24/16.
 *
 * Copyright 2016
 */
sealed abstract class Alphabet(val value: Char) extends CharEnumEntry

case object Alphabet extends CharEnum[Alphabet] {

  val values = findValues

  case object A extends Alphabet('A')
  case object B extends Alphabet('B')
  case object C extends Alphabet('C')
  case object D extends Alphabet('D')

}
