package enumeratum

import cats.{Eq, Hash, Show}

trait CatsEnum[A <: EnumEntry] { this: Enum[A] =>

  /** `Eq` instance for the enum entries - treats all enum values as distinct.
    */
  implicit val eqInstance: Eq[A] = Cats.eqForEnum[A]

  /** `Show` instance for the enum entries - returns the (transformed) entry name.
    */
  implicit val showInstance: Show[A] = Cats.showForEnum[A]

  /** `Hash` instance for the enum entries - based on entry name.
    */
  implicit val hashInstance: Hash[A] = Cats.hashForEnum[A]

}
