package enumeratum

sealed trait DummyEnum

object DummyEnum extends Enum[DummyEnum] {

  val values = findValues

  case object Hello extends DummyEnum
  case object GoodBye extends DummyEnum
  case object Hi extends DummyEnum

}

object Wrapper {

  sealed trait SmartEnum

  object SmartEnum extends Enum[SmartEnum] {

    val values = findValues

    case object Hello extends SmartEnum
    case object GoodBye extends SmartEnum
    case object Hi extends SmartEnum

  }

}