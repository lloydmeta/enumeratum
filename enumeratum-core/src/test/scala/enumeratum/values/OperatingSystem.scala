package enumeratum.values

/** Created by Lloyd on 8/4/16.
  *
  * Copyright 2016
  */
sealed abstract class OperatingSystem(val value: String) extends StringEnumEntry

case object OperatingSystem extends StringEnum[OperatingSystem] {

  val values = findValues

  case object Linux   extends OperatingSystem("linux")
  case object OSX     extends OperatingSystem("osx")
  case object Windows extends OperatingSystem("windows")
  case object Android extends OperatingSystem("android")

}
