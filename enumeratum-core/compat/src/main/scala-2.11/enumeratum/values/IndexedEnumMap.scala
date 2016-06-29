package enumeratum.values

/**
 * An [[ValueEnum]] that has an optimized array access that does not uses
 * boxing when accessing the methods [[ValueEnum.withValue()]] and [[ValueEnum.withValueOpt()]]
 */
sealed abstract class IndexedEnumMap[@specialized(Int, Short, Long) ValueType <: AnyVal, EntryType <: ValueEnumEntry[ValueType]](values: Seq[EntryType]) {

  private[this] val existingEntriesString = values.map(_.value).mkString(", ")
  private[this] val (arrayValues, minValue) = {
    val indices = values.map(v => toIndex(v.value))

    val minValue = indices.min
    val maxValue = indices.max

    val retVal = Array.fill[Option[EntryType]](maxValue - minValue + 1)(None)

    for (value <- values) {
      retVal(toIndex(value.value) - minValue) = Some(value)
    }

    (retVal, minValue)
  }

  /**
   * Tries to get an [[Int]] by the supplied value. The value corresponds to the .value
   * of the case objects implementing [[Int]]
   *
   * Like [[Enumeration]]'s `withValue`, this method will throw if the value does not match any of the values'
   * `.value` values.
   */
  final def withValue(i: ValueType): EntryType = {
    val index = toIndex(i) - minValue
    if (index >= arrayValues.length || index < 0) {
      throw new NoSuchElementException(buildNotFoundMessage(i))
    } else {
      arrayValues(index).getOrElse(throw new NoSuchElementException(buildNotFoundMessage(i)))
    }
  }

  /**
   * Converts the given value to [[Int]]
   *
   * @param value The value to convert
   * @return The converted value
   */
  @inline def toIndex(value: ValueType): Int

  /**
   * Optionally returns an [[Int]] for a given value.
   */
  final def withValueOpt(i: ValueType) = {
    val index = toIndex(i) - minValue
    if (index >= arrayValues.length || index < 0) None else arrayValues(index)
  }

  private final def buildNotFoundMessage(i: ValueType): String = {
    s"$i is not a member of ValueEnum ($values)"
  }

}

sealed class IntIndexedEnumMap[A <: ValueEnumEntry[Int]](values: Seq[A]) extends IndexedEnumMap[Int, A](values) {
  final override def toIndex(value: Int): Int = value
}

sealed class ShortIndexedEnumMap[A <: ValueEnumEntry[Short]](values: Seq[A]) extends IndexedEnumMap[Short, A](values) {
  final override def toIndex(value: Short): Int = value.toInt
}

sealed class LongIndexedEnumMap[A <: ValueEnumEntry[Long]](values: Seq[A]) extends IndexedEnumMap[Long, A](values) {
  final override def toIndex(value: Long): Int = value.toInt
}