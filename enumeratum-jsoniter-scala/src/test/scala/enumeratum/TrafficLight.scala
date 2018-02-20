package enumeratum

sealed trait TrafficLight extends EnumEntry
object TrafficLight extends Enum[TrafficLight] {
  case object Red    extends TrafficLight
  case object Yellow extends TrafficLight
  case object Green  extends TrafficLight

  val values = findValues
}
