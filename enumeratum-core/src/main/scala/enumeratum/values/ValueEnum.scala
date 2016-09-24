package enumeratum.values

import enumeratum.{ EnumMacros, ValueEnumMacros }

import scala.language.experimental.macros

sealed trait ValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {

  /**
   * Map of [[ValueType]] to [[EntryType]] members
   */
  final lazy val valuesToEntriesMap: Map[ValueType, EntryType] = values.map(v => v.value -> v).toMap

  /**
   * The sequence of values for your [[Enum]]. You will typically want
   * to implement this in your extending class as a `val` so that `withValue`
   * and friends are as efficient as possible.
   *
   * Feel free to implement this however you'd like (including messing around with ordering, etc) if that
   * fits your needs better.
   */
  def values: Seq[EntryType]

  /**
   * Tries to get an [[EntryType]] by the supplied value. The value corresponds to the .value
   * of the case objects implementing [[EntryType]]
   *
   * Like [[Enumeration]]'s `withValue`, this method will throw if the value does not match any of the values'
   * `.value` values.
   */
  def withValue(i: ValueType): EntryType = withValueOpt(i).getOrElse(throw new NoSuchElementException(buildNotFoundMessage(i)))

  /**
   * Optionally returns an [[EntryType]] for a given value.
   */
  def withValueOpt(i: ValueType): Option[EntryType] = valuesToEntriesMap.get(i)

  private lazy val existingEntriesString = values.map(_.value).mkString(", ")

  private def buildNotFoundMessage(i: ValueType): String = {
    s"$i is not a member of ValueEnum ($existingEntriesString)"
  }

}

/*
 * For the sake of keeping implementations of ValueEnums constrainted to a subset that we have tested to work relatively well,
 * the following traits are implementations of the sealed trait.
 *
 * There is a bit of repetition in order to supply the findValues method (esp in the comments) because we are using a macro
 * and macro invocations cannot provide implementations for a super class's abstract method
 */

object IntEnum {

  /**
   * Materializes an IntEnum for a given IntEnumEntry
   */
  implicit def materialiseIntValueEnum[EntryType <: IntEnumEntry]: IntEnum[EntryType] = macro EnumMacros.materializeEnumImpl[EntryType]

}
/**
 * Value enum with [[IntEnumEntry]] entries
 */
trait IntEnum[A <: IntEnumEntry] extends ValueEnum[Int, A] {

  /**
   * Method that returns a Seq of [[A]] objects that the macro was able to find.
   *
   * You will want to use this in some way to implement your [[values]] method. In fact,
   * if you aren't using this method...why are you even bothering with this lib?
   */
  protected def findValues: IndexedSeq[A] = macro ValueEnumMacros.findIntValueEntriesImpl[A]

}

object LongEnum {

  /**
   * Materializes a LongEnum for an scope LongEnumEntry
   */
  implicit def materialiseLongValueEnum[EntryType <: LongEnumEntry]: LongEnum[EntryType] = macro EnumMacros.materializeEnumImpl[EntryType]

}

/**
 * Value enum with [[LongEnumEntry]] entries
 */
trait LongEnum[A <: LongEnumEntry] extends ValueEnum[Long, A] {

  /**
   * Method that returns a Seq of [[A]] objects that the macro was able to find.
   *
   * You will want to use this in some way to implement your [[values]] method. In fact,
   * if you aren't using this method...why are you even bothering with this lib?
   */
  final protected def findValues: IndexedSeq[A] = macro ValueEnumMacros.findLongValueEntriesImpl[A]
}

object ShortEnum {

  /**
   * Materializes a ShortEnum for an inscope ShortEnumEntry
   */
  implicit def materialiseShortValueEnum[EntryType <: ShortEnumEntry]: ShortEnum[EntryType] = macro EnumMacros.materializeEnumImpl[EntryType]

}

/**
 * Value enum with [[ShortEnumEntry]] entries
 */
trait ShortEnum[A <: ShortEnumEntry] extends ValueEnum[Short, A] {

  /**
   * Method that returns a Seq of [[A]] objects that the macro was able to find.
   *
   * You will want to use this in some way to implement your [[values]] method. In fact,
   * if you aren't using this method...why are you even bothering with this lib?
   */
  final protected def findValues: IndexedSeq[A] = macro ValueEnumMacros.findShortValueEntriesImpl[A]
}

object StringEnum {

  /**
   * Materializes a StringEnum for an in-scope StringEnumEntry
   */
  implicit def materialiseStringValueEnum[EntryType <: StringEnumEntry]: StringEnum[EntryType] = macro EnumMacros.materializeEnumImpl[EntryType]

}

/**
 * Value enum with [[StringEnumEntry]] entries
 *
 * This is similar to [[enumeratum.Enum]], but different in that values must be
 * literal values. This restraint allows us to enforce uniqueness at compile time.
 *
 * Note that uniqueness is only guaranteed if you do not do any runtime string manipulation on values.
 */
trait StringEnum[A <: StringEnumEntry] extends ValueEnum[String, A] {

  /**
   * Method that returns a Seq of [[A]] objects that the macro was able to find.
   *
   * You will want to use this in some way to implement your [[values]] method. In fact,
   * if you aren't using this method...why are you even bothering with this lib?
   */
  final protected def findValues: IndexedSeq[A] = macro ValueEnumMacros.findStringValueEntriesImpl[A]
}

object ByteEnum {

  /**
   * Materializes a ByteEnum for an in-scope ByteEnumEntry
   */
  implicit def materialiseByteValueEnum[EntryType <: ByteEnumEntry]: ByteEnum[EntryType] = macro EnumMacros.materializeEnumImpl[EntryType]

}

/**
 * Value enum with [[ByteEnumEntry]] entries
 *
 * This is similar to [[enumeratum.Enum]], but different in that values must be
 * literal values. This restraint allows us to enforce uniqueness at compile time.
 *
 * Note that uniqueness is only guaranteed if you do not do any runtime string manipulation on values.
 */
trait ByteEnum[A <: ByteEnumEntry] extends ValueEnum[Byte, A] {

  /**
   * Method that returns a Seq of [[A]] objects that the macro was able to find.
   *
   * You will want to use this in some way to implement your [[values]] method. In fact,
   * if you aren't using this method...why are you even bothering with this lib?
   */
  final protected def findValues: IndexedSeq[A] = macro ValueEnumMacros.findByteValueEntriesImpl[A]
}

object CharEnum {

  /**
   * Materializes a CharEnum for an in-scope CharEnumEntry
   */
  implicit def materialiseCharValueEnum[EntryType <: CharEnumEntry]: CharEnum[EntryType] = macro EnumMacros.materializeEnumImpl[EntryType]

}

/**
 * Value enum with [[CharEnumEntry]] entries
 *
 * This is similar to [[enumeratum.Enum]], but different in that values must be
 * literal values. This restraint allows us to enforce uniqueness at compile time.
 *
 * Note that uniqueness is only guaranteed if you do not do any runtime string manipulation on values.
 */
trait CharEnum[A <: CharEnumEntry] extends ValueEnum[Char, A] {

  /**
   * Method that returns a Seq of [[A]] objects that the macro was able to find.
   *
   * You will want to use this in some way to implement your [[values]] method. In fact,
   * if you aren't using this method...why are you even bothering with this lib?
   */
  final protected def findValues: IndexedSeq[A] = macro ValueEnumMacros.findCharValueEntriesImpl[A]
}

