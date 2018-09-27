package enumeratum.values

import cats.{Eq, Order, Show}

trait CatsValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {
  this: ValueEnum[ValueType, EntryType] =>

  implicit val eqInstance: Eq[EntryType] = Eq.fromUniversalEquals[EntryType]

  implicit val showInstance: Show[EntryType] = Show.fromToString[EntryType]

}

trait CatsCustomOrderValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {
  this: ValueEnum[ValueType, EntryType] =>

  implicit val valueTypeOrder: Order[ValueType]

  implicit val orderInstance: Order[EntryType] = Order.by(_.value)

}

abstract class CatsOrderValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]](
    implicit override val valueTypeOrder: Order[ValueType])
    extends CatsCustomOrderValueEnum[ValueType, EntryType] {
  this: ValueEnum[ValueType, EntryType] =>
  // nothing needed here
}
