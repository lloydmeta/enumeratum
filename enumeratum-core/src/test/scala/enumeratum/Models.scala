package enumeratum

sealed trait DummyEnum extends EnumEntry

object DummyEnum extends Enum[DummyEnum] {

  val values = findValues

  case object Hello extends DummyEnum
  case object GoodBye extends DummyEnum
  case object Hi extends DummyEnum

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