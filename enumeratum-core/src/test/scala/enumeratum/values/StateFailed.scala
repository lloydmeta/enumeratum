package enumeratum.values

import enumeratum._

sealed abstract class StateFailed(override val entryName: String) extends EnumEntry

object StateFailed extends Enum[StateFailed] {

  val values = findValues // <- OK if replaced with lazy val

  val ALABAMA = "AL" // <- OK if replaced with final val
  val ALASKA = "AK" // <- OK if replaced with final val

  case object Alabama extends StateFailed(ALABAMA)
  case object Alaska  extends StateFailed(ALASKA)

  // val values = findValues // <- OK if moved here
}
