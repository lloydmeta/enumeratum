package enumeratum

import enumeratum.EnumEntry.UpperHyphencase

import scala.collection.immutable.IndexedSeq

/** Created by Lloyd on 2/5/17.
  *
  * Copyright 2017
  */
sealed abstract class StackedEnum extends EnumEntry with UpperHyphencase

case object StackedEnum extends Enum[StackedEnum] {

  val values: IndexedSeq[StackedEnum] = findValues

  case object SomethingRedAndSmall extends StackedEnum
  case object SomethingBlueAndBig  extends StackedEnum
}
