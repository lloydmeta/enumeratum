package enumeratum.values

/**
 * Created by Lloyd on 9/24/16.
 *
 * Copyright 2016
 */
sealed abstract class Bites(val value: Byte) extends ByteEnumEntry

object Bites extends ByteEnum[Bites] {
  val values = findValues

  case object OneByte extends Bites(1)
  case object TwoByte extends Bites(2)
  case object ThreeByte extends Bites(3)
  case object FourByte extends Bites(4)
}
