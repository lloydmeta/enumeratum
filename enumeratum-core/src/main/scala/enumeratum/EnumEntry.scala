package enumeratum

import java.util.regex.Pattern

/**
  * Base type for an enum entry for [[Enum]]
  *
  * By default, the entryName method used for serialising and deseralising Enum values uses
  * toString, but feel free to override to fit your needs.
  *
  * Mix in the supplied stackable traits to convert the entryName to [[EnumEntry.Snakecase Snakecase]],
  * [[EnumEntry.Uppercase Uppercase]], and [[EnumEntry.Lowercase Lowercase]]
  */
abstract class EnumEntry {

  /**
    * String representation of this Enum Entry.
    *
    * Override in your implementation if needed
    */
  def entryName: String = toString

}

object EnumEntry {

  /*
   * Compiled Regular expressions for performance
   *
   * http://stackoverflow.com/a/19832063/1814775
   */
  private val snakifyRegexp1     = Pattern.compile("([A-Z]+)([A-Z][a-z])")
  private val snakifyRegexp2     = Pattern.compile("([a-z\\d])([A-Z])")
  private val snakifyReplacement = "$1_$2"

  /**
    * Stackable trait to convert the entryName to snake_case. For UPPER_SNAKE_CASE,
    * also mix in [[Uppercase]] after this one.
    */
  trait Snakecase extends EnumEntry {
    abstract override def entryName: String = camel2snake(super.entryName)

    // Taken from Lift's StringHelpers#snakify https://github.com/lift/framework/blob/a3075e0676d60861425281427aa5f57c02c3b0bc/core/util/src/main/scala/net/liftweb/util/StringHelpers.scala#L91
    private def camel2snake(name: String) = {
      val first = snakifyRegexp1.matcher(name).replaceAll(snakifyReplacement)
      snakifyRegexp2.matcher(first).replaceAll(snakifyReplacement).toLowerCase
    }
  }

  /**
    * Stackable trait to convert the entryName to UPPERCASE.
    */
  trait Uppercase extends EnumEntry {
    abstract override def entryName: String = super.entryName.toUpperCase
  }

  /**
    * Stackable trait to convert the entryName to lowercase.
    */
  trait Lowercase extends EnumEntry {
    abstract override def entryName: String = super.entryName.toLowerCase
  }

  /**
    * Helper implicit class that holds enrichment methods
    */
  implicit class EnumEntryOps[A <: EnumEntry](val enumEntry: A) extends AnyVal {

    /**
      * Checks if the current enum value is contained by the set of enum values in the parameter list.
      *
      * @param firstEntry First enum of the list.
      * @param otherEnums Remaining enums.
      * @return `true` if the current value is contained by the parameter list.
      */
    def in(firstEntry: A, otherEnums: A*): Boolean =
      in(firstEntry +: otherEnums)

    /**
      * Checks if the current enum value is contained by the set of enum values in the parameter list.
      *
      * @param entries First enum of the list.
      * @return `true` if the current value is contained by the parameter list.
      */
    def in(entries: Seq[A]): Boolean = entries.contains(enumEntry)
  }

}
