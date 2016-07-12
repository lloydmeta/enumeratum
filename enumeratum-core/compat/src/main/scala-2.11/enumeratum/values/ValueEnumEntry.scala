package enumeratum.values

/**
 * Created by Lloyd on 4/11/16.
 *
 * Copyright 2016
 */

sealed trait ValueEnumEntry[ValueType <: AnyVal] {

  /**
   * Value of this entry
   */
  def value: ValueType

}

object ValueEnumEntry {

  implicit class ValueEnumOps[A <: ValueEnumEntry[_]](val enumEntry: A) {

    /**
     * Checks if the current enum value is contained by the set of enum values in the parameter list.
     *
     * @param firstEnum First enum of the list.
     * @param otherEnums Remaining enums.
     * @return `true` if the current value is contained by the parameter list.
     */
    def in(firstEnum: A, otherEnums: A*): Boolean = in(firstEnum +: otherEnums)

    /**
     * Checks if the current enum value is contained by the set of enum values in the parameter list.
     *
     * @param entries First enum of the list.
     * @return `true` if the current value is contained by the parameter list.
     */
    def in(entries: Seq[A]): Boolean = entries.contains(enumEntry)

  }
}

/**
 * Value Enum Entry parent class for [[Int]] valued entries
 */
abstract class IntEnumEntry extends ValueEnumEntry[Int]

/**
 * Value Enum Entry parent class for [[Long]] valued entries
 */
abstract class LongEnumEntry extends ValueEnumEntry[Long]

/**
 * Value Enum Entry parent class for [[Short]] valued entries
 */
abstract class ShortEnumEntry extends ValueEnumEntry[Short]