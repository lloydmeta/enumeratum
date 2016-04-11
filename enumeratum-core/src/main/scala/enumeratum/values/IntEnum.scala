package enumeratum.values

import enumeratum.EnumMacros

import scala.language.experimental.macros

/**
  * Created by Lloyd on 4/11/16.
  *
  * Copyright 2016
  */
trait IntEnum[A <: IntEnumEntry] {

  /**
    * Map of [[A]] object names to [[A]]s
    */
  lazy final val intToValuesMap: Map[Int, A] = values.map(v => v.value -> v).toMap

  /**
    * Map of [[A]] to their index in the values sequence.
    *
    * A performance optimisation so that indexOf can be found in constant time.
    */
  lazy final val valuesToIndex: Map[A, Int] = values.zipWithIndex.toMap

  def findValues: IndexedSeq[A] = macro EnumMacros.findIntValuesImp[A]

  /**
    * The sequence of values for your [[Enum]]. You will typically want
    * to implement this in your extending class as a `val` so that `withName`
    * and friends are as efficient as possible.
    *
    * Feel free to implement this however you'd like (including messing around with ordering, etc) if that
    * fits your needs better.
    */
  def values: Seq[A]

}
