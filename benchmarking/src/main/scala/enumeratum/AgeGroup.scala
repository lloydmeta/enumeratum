package enumeratum

sealed trait AgeGroup extends EnumEntry

case object AgeGroup extends Enum[AgeGroup] {

  val values = findValues

  case object Baby extends AgeGroup
  case object Toddler extends AgeGroup
  case object Teenager extends AgeGroup
  case object Adult extends AgeGroup
  case object Senior extends AgeGroup

}