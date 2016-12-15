package enumeratum

import java.util.regex.Pattern

/**
  * Base type for an enum entry for [[Enum]]
  *
  * By default, the entryName method used for serialising and deseralising Enum values uses
  * toString, but feel free to override to fit your needs.
  *
  * Mix in the supplied stackable traits to convert the entryName to [[EnumEntry.Snakecase Snakecase]],
  * [[EnumEntry.Uppercase Uppercase]], [[EnumEntry.Lowercase Lowercase]] etc.
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
  private val regexp1     = Pattern.compile("([A-Z]+)([A-Z][a-z])")
  private val regexp2     = Pattern.compile("([a-z\\d])([A-Z])")
  private val replacement = "$1_$2"

  // Adapted from Lift's StringHelpers#snakify https://github.com/lift/framework/blob/a3075e0676d60861425281427aa5f57c02c3b0bc/core/util/src/main/scala/net/liftweb/util/StringHelpers.scala#L91
  private def camel2WordArray(name: String) = {
    val first = regexp1.matcher(name).replaceAll(replacement)
    regexp2.matcher(first).replaceAll(replacement).split("_")
  }

  /**
    * Stackable trait to convert the entryName to Capital_Snake_Case .
    */
  trait CapitalSnakecase extends EnumEntry {
    abstract override def entryName: String =
      camel2WordArray(super.entryName).mkString("_")
  }

  /**
    * Stackable trait to convert the entryName to Capital-Hyphen-Case.
    */
  trait CapitalHyphencase extends EnumEntry {
    abstract override def entryName: String =
      camel2WordArray(super.entryName).mkString("-")
  }

  /**
    * Stackable trait to convert the entryName to Capital.Dot.Case.
    */
  trait CapitalDotcase extends EnumEntry {
    abstract override def entryName: String =
      camel2WordArray(super.entryName).mkString(".")
  }

  /**
    * Stackable trait to convert the entryName to Capital Words.
    */
  trait CapitalWords extends EnumEntry {
    abstract override def entryName: String =
      camel2WordArray(super.entryName).mkString(" ")
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
    * Stackable trait to convert the entryName to snake_case.
    */
  trait Snakecase extends EnumEntry with CapitalSnakecase with Lowercase

  /**
    * Stackable trait to convert the entryName to UPPER_SNAKE_CASE
    */
  trait UpperSnakecase extends EnumEntry with CapitalSnakecase with Uppercase

  /**
    * Stackable trait to convert the entryName to hyphen-case.
    */
  trait Hyphencase extends EnumEntry with CapitalHyphencase with Lowercase

  /**
    * Stackable trait to convert the entryName to UPPER-HYPHEN-CASE.
    */
  trait UpperHyphencase extends EnumEntry with CapitalHyphencase with Uppercase

  /**
    * Stackable trait to convert the entryName to dot.case.
    */
  trait Dotcase extends EnumEntry with CapitalDotcase with Lowercase

  /**
    * Stackable trait to convert the entryName to UPPER.DOT.CASE
    */
  trait UpperDotcase extends EnumEntry with CapitalDotcase with Uppercase

  /**
    * Stackable trait to convert the entryName to words.
    */
  trait Words extends EnumEntry with CapitalWords with Lowercase

  /**
    * Stackable trait to convert the entryName to UPPER WORDS.
    */
  trait UpperWords extends EnumEntry with CapitalWords with Uppercase

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
