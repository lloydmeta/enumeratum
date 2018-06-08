package enumeratum

import enumeratum.values._

sealed trait TrafficLight extends EnumEntry
object TrafficLight extends Enum[TrafficLight] {
  case object Red    extends TrafficLight
  case object Yellow extends TrafficLight
  case object Green  extends TrafficLight

  val values = findValues
}

sealed abstract class TrafficLightByInt(val value: Int) extends IntEnumEntry
object TrafficLightByInt extends IntEnum[TrafficLightByInt] {
  val values = findValues

  case object Red    extends TrafficLightByInt(1)
  case object Yellow extends TrafficLightByInt(2)
  case object Green  extends TrafficLightByInt(3)
}
