package enumeratum

/** Created by alonsodomin on 14/10/2016.
  */
sealed trait TrafficLight extends EnumEntry
object TrafficLight extends Enum[TrafficLight] with ArgonautEnum[TrafficLight] {
  case object Red    extends TrafficLight
  case object Yellow extends TrafficLight
  case object Green  extends TrafficLight

  val values = findValues
}
