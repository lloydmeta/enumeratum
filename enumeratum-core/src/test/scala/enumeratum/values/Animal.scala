package enumeratum.values

/**
 * Created by Lloyd on 8/22/16.
 *
 * Copyright 2016
 */
sealed abstract class Animal(val value: Long) extends LongEnumEntry

case object Animal extends LongEnum[Animal] {

  val values = findValues

  case object Plant extends Animal(1L)
  case object Reptile extends Animal(2L)
  case object Mammal extends Animal(3L)

  sealed abstract class Mammalian(val value: Int) extends IntEnumEntry

  object Mammalian extends IntEnum[Mammalian] {

    val values = findValues

    case object Dog extends Mammalian(1)
    case object Cat extends Mammalian(2)
    case object Whale extends Mammalian(3)
    case object Mouse extends Mammalian(4)
    case object Human extends Mammalian(5)

  }
}
