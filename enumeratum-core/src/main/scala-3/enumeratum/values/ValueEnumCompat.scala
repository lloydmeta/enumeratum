package enumeratum.values

import scala.language.experimental.macros

import _root_.enumeratum.{Enum, EnumMacros, ValueEnumMacros}
import _root_.enumeratum.compat

private[enumeratum] trait IntEnumCompanion {

  /** Materializes an `IntEnum` for a given `IntEnumEntry`. */
  implicit inline def materialiseIntValueEnum[EntryType <: IntEnumEntry]: IntEnum[EntryType] = ${
    EnumMacros.materializeEnumImpl[EntryType, IntEnum[EntryType]]
  }
  implicit def materialiseIntValueEnum[EntryType <: IntEnumEntry]: IntEnum[EntryType] = macro
    compat.EnumMacros.materializeEnumImpl[EntryType]
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
  protected def findValues: IndexedSeq[A] = macro compat.ValueEnumMacros.findIntValueEntriesImpl[A]
  // format: on
}

private[enumeratum] trait LongEnumCompanion {

  /** Materializes a LongEnum for an scope LongEnumEntry
    */
  implicit inline def materialiseLongValueEnum[EntryType <: LongEnumEntry]: LongEnum[EntryType] = ${
    EnumMacros.materializeEnumImpl[EntryType, LongEnum[EntryType]]
  }
  implicit def materialiseLongValueEnum[EntryType <: LongEnumEntry]: LongEnum[EntryType] = macro
    compat.EnumMacros.materializeEnumImpl[EntryType]
}

private[enumeratum] trait LongEnumCompat[A <: LongEnumEntry] { _enum: LongEnum[A] =>

  // format: off
  /** Returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method...why are you even bothering with this lib?
    */
  protected inline def findValues: IndexedSeq[A] = ${ ValueEnumMacros.findLongValueEntriesImpl[A] }
  protected def findValues: IndexedSeq[A] = macro compat.ValueEnumMacros.findLongValueEntriesImpl[A]
  // format: on
}

private[enumeratum] trait ShortEnumCompanion {

  /** Materializes a ShortEnum for an in-scope ShortEnumEntry
    */
  implicit inline def materialiseShortValueEnum[EntryType <: ShortEnumEntry]: ShortEnum[EntryType] =
    ${
      EnumMacros.materializeEnumImpl[EntryType, ShortEnum[EntryType]]
    }
  implicit def materialiseShortValueEnum[EntryType <: ShortEnumEntry]: ShortEnum[EntryType] = macro
    compat.EnumMacros.materializeEnumImpl[EntryType]
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
  protected def findValues: IndexedSeq[A] = macro
    compat.ValueEnumMacros.findShortValueEntriesImpl[A]
}

private[enumeratum] trait StringEnumCompanion {

  /** Materializes a StringEnum for an in-scope StringEnumEntry
    */
  implicit inline def materialiseStringValueEnum[EntryType <: StringEnumEntry]
      : StringEnum[EntryType] = ${
    EnumMacros.materializeEnumImpl[EntryType, StringEnum[EntryType]]
  }
  implicit def materialiseStringValueEnum[EntryType <: StringEnumEntry]: StringEnum[
    EntryType
  ] = macro compat.EnumMacros.materializeEnumImpl[EntryType]
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
  protected def findValues: IndexedSeq[A] = macro
    compat.ValueEnumMacros.findStringValueEntriesImpl[A]
  // format: on
}

private[enumeratum] trait ByteEnumCompanion {

  /** Materializes a ByteEnum for an in-scope ByteEnumEntry
    */
  implicit inline def materialiseByteValueEnum[EntryType <: ByteEnumEntry]: ByteEnum[EntryType] = ${
    EnumMacros.materializeEnumImpl[EntryType, ByteEnum[EntryType]]
  }
  implicit def materialiseByteValueEnum[EntryType <: ByteEnumEntry]: ByteEnum[EntryType] = macro
    compat.EnumMacros.materializeEnumImpl[EntryType]
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
  protected def findValues: IndexedSeq[A] = macro compat.ValueEnumMacros.findByteValueEntriesImpl[A]
  // format: on
}

private[enumeratum] trait CharEnumCompanion {

  /** Materializes a CharEnum for an in-scope CharEnumEntry
    */
  implicit inline def materialiseCharValueEnum[EntryType <: CharEnumEntry]: CharEnum[EntryType] = ${
    EnumMacros.materializeEnumImpl[EntryType, CharEnum[EntryType]]
  }
  implicit def materialiseCharValueEnum[EntryType <: CharEnumEntry]: CharEnum[EntryType] = macro
    compat.EnumMacros.materializeEnumImpl[EntryType]
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
  protected def findValues: IndexedSeq[A] = macro compat.ValueEnumMacros.findCharValueEntriesImpl[A]
  // format: on
}
