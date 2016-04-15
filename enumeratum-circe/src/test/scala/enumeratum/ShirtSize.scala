package enumeratum

/**
 * Created by Lloyd on 4/14/16.
 *
 * Copyright 2016
 */

sealed trait ShirtSize extends EnumEntry

case object ShirtSize extends CirceEnum[ShirtSize] with Enum[ShirtSize] {

  case object Small extends ShirtSize
  case object Medium extends ShirtSize
  case object Large extends ShirtSize

  val values = findValues

}