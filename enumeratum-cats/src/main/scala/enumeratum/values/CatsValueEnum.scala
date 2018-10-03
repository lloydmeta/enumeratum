package enumeratum.values

import cats.{Eq, Order, Show}

trait CatsValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {
  this: ValueEnum[ValueType, EntryType] =>

  /**
    * `Eq` instance for the enum entries - treats all enum values as distinct.
    */
  implicit val eqInstance: Eq[EntryType] = Cats.eqForEnum[EntryType]

  /**
    * Builds a `Show` instance based on `toString`.
    */
  implicit val showInstance: Show[EntryType] = Cats.showForEnum[EntryType]

}

trait CatsCustomOrderValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {
  this: ValueEnum[ValueType, EntryType] =>

  /**
    * Order for the enum's value type - used to derive [[orderInstance]].
    */
  implicit val valueTypeOrder: Order[ValueType]

  /**
    * Builds a `Order` instance from the given `Order` (see [[valueTypeOrder]] on the value type.
    */
  implicit val orderInstance: Order[EntryType] = Cats.valueOrderForEnum[EntryType, ValueType](valueTypeOrder)

}

abstract class CatsOrderValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]](
    implicit override val valueTypeOrder: Order[ValueType])
    extends CatsCustomOrderValueEnum[ValueType, EntryType] {
  this: ValueEnum[ValueType, EntryType] =>
  // nothing needed here
}
