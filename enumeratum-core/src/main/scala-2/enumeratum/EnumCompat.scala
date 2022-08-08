package enumeratum

import scala.language.experimental.macros

private[enumeratum] trait EnumCompat[A <: EnumEntry] { _: Enum[A] =>

  /** Returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method...why are you even bothering with this lib?
    */
  protected def findValues: IndexedSeq[A] =
    macro EnumMacros.findValuesImpl[A]
}

private[enumeratum] trait EnumCompanion {

  /** Finds the `Enum` companion object for a particular `EnumEntry`. */
  implicit def materializeEnum[A <: EnumEntry]: Enum[A] =
    macro EnumMacros.materializeEnumImpl[A]
}
