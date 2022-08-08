package enumeratum

private[enumeratum] trait EnumCompat[A <: EnumEntry] { _enum: Enum[A] =>
  inline def findValues: IndexedSeq[A] = ${ EnumMacros.findValuesImpl[A] }
}

private[enumeratum] trait EnumCompanion {

  /** Finds the `Enum` companion object for a particular `EnumEntry`. */
  implicit inline def materializeEnum[A <: EnumEntry]: Enum[A] =
    ${ EnumMacros.materializeEnumImpl[A, Enum[A]] }

}
