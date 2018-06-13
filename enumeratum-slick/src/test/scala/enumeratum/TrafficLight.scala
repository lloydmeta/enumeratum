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

sealed abstract class ShortValueExample(val value: Short) extends ShortEnumEntry
object ShortValueExample extends ShortEnum[ShortValueExample] {
  val values = findValues

  case object Zero extends ShortValueExample(0)
  case object One  extends ShortValueExample(1)
  case object Two  extends ShortValueExample(2)
}
sealed abstract class StringValueExample(val value: String) extends StringEnumEntry
object StringValueExample extends StringEnum[StringValueExample] {
  val values = findValues

  case object Zero extends StringValueExample("0")
  case object One  extends StringValueExample("1")
  case object Two  extends StringValueExample("2")
}
