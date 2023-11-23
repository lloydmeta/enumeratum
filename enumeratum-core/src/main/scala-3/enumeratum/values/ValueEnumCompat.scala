package enumeratum.values

import scala.language.experimental.macros

import _root_.enumeratum.{Enum, EnumMacros, ValueEnumMacros}

private[enumeratum] trait IntEnumCompanion {

  /** Materializes an `IntEnum` for a given `IntEnumEntry`. */
  implicit inline def materialiseIntValueEnum[EntryType <: IntEnumEntry]: IntEnum[EntryType] = ${
    EnumMacros.materializeEnumImpl[EntryType, IntEnum[EntryType]]
  }
}

private[enumeratum] trait IntEnumCompat[A <: IntEnumEntry] { _enum: IntEnum[A] =>

  // format: off
  /** Returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method...why are you even bothering with this lib?
    */
  protected inline def findValues: IndexedSeq[A] =
    ${ ValueEnumMacros.findIntValueEntriesImpl[A] }
  // format: on
}

private[enumeratum] trait LongEnumCompanion {

  /** Materializes a LongEnum for an scope LongEnumEntry
    */
  implicit inline def materialiseLongValueEnum[EntryType <: LongEnumEntry]: LongEnum[EntryType] = ${
    EnumMacros.materializeEnumImpl[EntryType, LongEnum[EntryType]]
  }
}

private[enumeratum] trait LongEnumCompat[A <: LongEnumEntry] { _enum: LongEnum[A] =>

  // format: off
  /** Returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method...why are you even bothering with this lib?
    */
  protected inline def findValues: IndexedSeq[A] = ${ ValueEnumMacros.findLongValueEntriesImpl[A] }
  // format: on
}

private[enumeratum] trait ShortEnumCompanion {

  /** Materializes a ShortEnum for an in-scope ShortEnumEntry
    */
  implicit inline def materialiseShortValueEnum[EntryType <: ShortEnumEntry]: ShortEnum[EntryType] =
    ${
      EnumMacros.materializeEnumImpl[EntryType, ShortEnum[EntryType]]
    }
}

private[enumeratum] trait ShortEnumCompat[A <: ShortEnumEntry] { _enum: ShortEnum[A] =>

  /** Returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method...why are you even bothering with this lib?
    */
  protected inline def findValues: IndexedSeq[A] = ${
    ValueEnumMacros.findShortValueEntriesImpl[A]
  }
}

private[enumeratum] trait StringEnumCompanion {

  /** Materializes a StringEnum for an in-scope StringEnumEntry
    */
  implicit inline def materialiseStringValueEnum[EntryType <: StringEnumEntry]
      : StringEnum[EntryType] = ${
    EnumMacros.materializeEnumImpl[EntryType, StringEnum[EntryType]]
  }
}

private[enumeratum] trait StringEnumCompat[A <: StringEnumEntry] { _enum: StringEnum[A] =>

  // format: off
  /** Returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method...why are you even bothering with this lib?
    */
  protected inline def findValues: IndexedSeq[A] = ${
    ValueEnumMacros.findStringValueEntriesImpl[A]
  }
  // format: on
}

private[enumeratum] trait ByteEnumCompanion {

  /** Materializes a ByteEnum for an in-scope ByteEnumEntry
    */
  implicit inline def materialiseByteValueEnum[EntryType <: ByteEnumEntry]: ByteEnum[EntryType] = ${
    EnumMacros.materializeEnumImpl[EntryType, ByteEnum[EntryType]]
  }
}

private[enumeratum] trait ByteEnumCompat[A <: ByteEnumEntry] { _enum: ByteEnum[A] =>

  // format: off
  /** Returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method...why are you even bothering with this lib?
    */
  protected inline def findValues: IndexedSeq[A] = ${
    ValueEnumMacros.findByteValueEntriesImpl[A]
  }
  // format: on
}

private[enumeratum] trait CharEnumCompanion {

  /** Materializes a CharEnum for an in-scope CharEnumEntry
    */
  implicit inline def materialiseCharValueEnum[EntryType <: CharEnumEntry]: CharEnum[EntryType] = ${
    EnumMacros.materializeEnumImpl[EntryType, CharEnum[EntryType]]
  }
}

private[enumeratum] trait CharEnumCompat[A <: CharEnumEntry] { _enum: CharEnum[A] =>

  // format: off
  /** Returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method...why are you even bothering with this lib?
    */
  protected inline def findValues: IndexedSeq[A] = ${
    ValueEnumMacros.findCharValueEntriesImpl[A]
  }
  // format: on
}
