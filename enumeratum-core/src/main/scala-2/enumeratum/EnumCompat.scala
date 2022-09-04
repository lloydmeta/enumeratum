package enumeratum

import scala.language.experimental.macros

private[enumeratum] trait EnumCompat[A <: EnumEntry] { _: Enum[A] =>

  /** Returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method... why are you even bothering with this lib?
    */
  protected def findValues: IndexedSeq[A] =
    macro EnumMacros.findValuesImpl[A]

  /** The sequence of values for your [[Enum]]. You will typically want to implement this in your
    * extending class as a `val` so that `withName` and friends are as efficient as possible.
    *
    * Feel free to implement this however you'd like (including messing around with ordering, etc)
    * if that fits your needs better.
    */
  def values: IndexedSeq[A]
}

private[enumeratum] trait EnumCompanion {

  /** Finds the `Enum` companion object for a particular `EnumEntry`. */
  implicit def materializeEnum[A <: EnumEntry]: Enum[A] =
    macro EnumMacros.materializeEnumImpl[A]
}
