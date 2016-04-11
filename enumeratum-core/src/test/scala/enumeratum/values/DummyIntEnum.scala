package enumeratum.values

/**
  * Created by Lloyd on 4/11/16.
  *
  * Copyright 2016
  */

sealed abstract class DummyIntEnum(val value: Int) extends IntEnumEntry

case object DummyIntEnum extends IntEnum[DummyIntEnum] {

  case object Book extends DummyIntEnum(1)
  case object Movie extends DummyIntEnum(2)

  val values = findValues

}
