package enumeratum.values

import scala.language.experimental.macros

import _root_.enumeratum.{Enum, EnumMacros, ValueEnumMacros}

private[enumeratum] trait IntEnumCompanion {

  /** Materializes an IntEnum for a given IntEnumEntry
    */
  implicit def materialiseIntValueEnum[EntryType <: IntEnumEntry]: IntEnum[EntryType] =
    macro EnumMacros.materializeEnumImpl[EntryType]
}

private[enumeratum] trait IntEnumCompat[A <: IntEnumEntry] { _: IntEnum[A] =>

  /** Returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method...why are you even bothering with this lib?
    */
  protected def findValues: IndexedSeq[A] =
    macro ValueEnumMacros.findIntValueEntriesImpl[A]
}

private[enumeratum] trait LongEnumCompanion {

  /** Materializes a LongEnum for an scope LongEnumEntry
    */
  implicit def materialiseLongValueEnum[EntryType <: LongEnumEntry]: LongEnum[EntryType] =
    macro EnumMacros.materializeEnumImpl[EntryType]

}

private[enumeratum] trait LongEnumCompat[A <: LongEnumEntry] { _: LongEnum[A] =>

  /** Returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method...why are you even bothering with this lib?
    */
  protected def findValues: IndexedSeq[A] =
    macro ValueEnumMacros.findLongValueEntriesImpl[A]
}

private[enumeratum] trait ShortEnumCompanion {

  /** Materializes a ShortEnum for an in-scope ShortEnumEntry
    */
  implicit def materialiseShortValueEnum[EntryType <: ShortEnumEntry]: ShortEnum[EntryType] =
    macro EnumMacros.materializeEnumImpl[EntryType]
}

private[enumeratum] trait ShortEnumCompat[A <: ShortEnumEntry] { _: ShortEnum[A] =>

  /** Returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method...why are you even bothering with this lib?
    */
  protected def findValues: IndexedSeq[A] =
    macro ValueEnumMacros.findShortValueEntriesImpl[A]
}

private[enumeratum] trait StringEnumCompanion {

  /** Materializes a StringEnum for an in-scope StringEnumEntry
    */
  implicit def materialiseStringValueEnum[EntryType <: StringEnumEntry]: StringEnum[EntryType] =
    macro EnumMacros.materializeEnumImpl[EntryType]

}

private[enumeratum] trait StringEnumCompat[A <: StringEnumEntry] { _: StringEnum[A] =>

  /** Returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method...why are you even bothering with this lib?
    */
  protected def findValues: IndexedSeq[A] =
    macro ValueEnumMacros.findStringValueEntriesImpl[A]
}

private[enumeratum] trait ByteEnumCompanion {

  /** Materializes a ByteEnum for an in-scope ByteEnumEntry
    */
  implicit def materialiseByteValueEnum[EntryType <: ByteEnumEntry]: ByteEnum[EntryType] =
    macro EnumMacros.materializeEnumImpl[EntryType]

}

private[enumeratum] trait ByteEnumCompat[A <: ByteEnumEntry] { _: ByteEnum[A] =>

  /** Returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method...why are you even bothering with this lib?
    */
  protected def findValues: IndexedSeq[A] =
    macro ValueEnumMacros.findByteValueEntriesImpl[A]
}

private[enumeratum] trait CharEnumCompanion {

  /** Materializes a CharEnum for an in-scope CharEnumEntry
    */
  implicit def materialiseCharValueEnum[EntryType <: CharEnumEntry]: CharEnum[EntryType] =
    macro EnumMacros.materializeEnumImpl[EntryType]

}

private[enumeratum] trait CharEnumCompat[A <: CharEnumEntry] { _: CharEnum[A] =>

  /** Returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method...why are you even bothering with this lib?
    */
  protected def findValues: IndexedSeq[A] =
    macro ValueEnumMacros.findCharValueEntriesImpl[A]
}
