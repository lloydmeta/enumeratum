package enumeratum.values

/** Created by Lloyd on 7/13/16.
  *
  * Copyright 2016
  */
sealed abstract class Size(val value: Int) extends IntEnumEntry

object Size extends IntEnum[Size] {

  val values = findValues

  case object Small  extends Size(1)
  case object Medium extends Size(2)
  case object Large  extends Size(3)

}
