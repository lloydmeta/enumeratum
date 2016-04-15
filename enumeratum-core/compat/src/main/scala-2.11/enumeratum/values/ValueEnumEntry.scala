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