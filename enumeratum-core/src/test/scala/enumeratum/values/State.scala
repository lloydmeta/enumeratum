package enumeratum.values

import enumeratum._

sealed abstract class State(override val entryName: String) extends EnumEntry

object State extends Enum[State] {

  val values = findValues

  case object Alabama extends State("AL")
  case object Alaska  extends State("AK")
}
