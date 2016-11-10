package enumeratum

import enumeratum.EnumEntry._

sealed trait EmptyEnum extends EnumEntry

object EmptyEnum extends Enum[EmptyEnum] {
  val values = findValues
}

sealed trait DummyEnum extends EnumEntry

object DummyEnum extends Enum[DummyEnum] {

  val values = findValues

  case object Hello extends DummyEnum
  case object GoodBye extends DummyEnum
  case object Hi extends DummyEnum

}

sealed trait SnakeEnum extends EnumEntry with Snakecase

object SnakeEnum extends Enum[SnakeEnum] {

  val values = findValues

  case object Hello extends SnakeEnum
  case object GoodBye extends SnakeEnum
  case object ShoutGoodBye extends SnakeEnum with Uppercase

}

sealed trait LowerEnum extends EnumEntry with Lowercase

object LowerEnum extends Enum[LowerEnum] {

  val values = findValues

  case object Hello extends LowerEnum
  case object GoodBye extends LowerEnum
  case object Sike extends LowerEnum with Uppercase

}

object Wrapper {

  sealed trait SmartEnum extends EnumEntry

  object SmartEnum extends Enum[SmartEnum] {

    val values = findValues

    case object Hello extends SmartEnum
    case object GoodBye extends SmartEnum
    case object Hi extends SmartEnum

  }

}

object InTheWoods {
  sealed abstract class Mushroom(val toxic: Boolean) extends EnumEntry

  object Mushroom extends Enum[Mushroom] {

    val values = findValues

    case object FlyAgaric extends Mushroom(true)
    case object LSD extends Mushroom(false)
    case object Shimeji extends Mushroom(false)

  }
}
