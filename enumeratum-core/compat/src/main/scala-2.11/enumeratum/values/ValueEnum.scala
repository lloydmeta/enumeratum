package enumeratum.values

import enumeratum.ValueEnumMacros
import scala.language.experimental.macros

sealed trait ValueEnum[@specialized(Short, Int) ValueType <: AnyVal, EntryType <: ValueEnumEntry[ValueType]] {

  /**
   * Map of [[ValueType]] to [[EntryType]] members
   */
  final lazy val valuesToEntriesMap: Map[ValueType, EntryType] = values.map(v => v.value -> v).toMap
  private lazy val existingEntriesString = values.map(_.value).mkString(", ")

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

  protected def buildNotFoundMessage(i: ValueType): String = {
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