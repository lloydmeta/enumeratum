package enumeratum

import java.util.regex.Pattern

/** Base type for an enum entry for [[Enum]]
  *
  * By default, the entryName method used for serialising and deserialising Enum values uses
  * toString, but feel free to override to fit your needs.
  *
  * Mix in the supplied stackable traits to convert the entryName to
  * [[EnumEntry.Snakecase Snakecase]], [[EnumEntry.Uppercase Uppercase]],
  * [[EnumEntry.Lowercase Lowercase]] etc.
  */
trait EnumEntry {

  /** String representation of this Enum Entry.
    *
    * Override in your implementation if needed
    */
  def entryName: String = stableEntryName

  private[this] lazy val stableEntryName: String = toString

}

object EnumEntry {

  /*
   * Compiled Regular expressions for performance
   *
   * http://stackoverflow.com/a/19832063/1814775
   */
  private val regexp1: Pattern    = Pattern.compile("([A-Z]+)([A-Z][a-z])")
  private val regexp2: Pattern    = Pattern.compile("([a-z\\d])([A-Z])")
  private val replacement: String = "$1_$2"

  // Adapted from Lift's StringHelpers#snakify https://github.com/lift/framework/blob/a3075e0676d60861425281427aa5f57c02c3b0bc/core/util/src/main/scala/net/liftweb/util/StringHelpers.scala#L91
  private def camel2WordArray(name: String): Array[String] = {
    val first = regexp1.matcher(name).replaceAll(replacement)
    regexp2.matcher(first).replaceAll(replacement).split("_")
  }

  private def capitalise(str: String): String = {
    if (str.isEmpty) str
    else str.take(1).toUpperCase + str.drop(1)
  }

  private def uncapitalise(str: String): String = {
    str.take(1).toLowerCase + str.drop(1)
  }

  /*
    A bunch of helpful traits for manipulating entry names with minimal boilerplate.

    Note that each override is followed by a lazy val that holds the name. This is an optimisation
    that has been shown to improve speeds by several hundred times when calling entryName
    (1499.862ns/call before vs 3.180ns/call for something like UpperHyphencase).
   */

  /** Stackable trait to convert the entryName to Capital_Snake_Case .
    */
  trait CapitalSnakecase extends EnumEntry {
    override def entryName: String = stableEntryName

    private[this] lazy val stableEntryName: String = camel2WordArray(super.entryName).mkString("_")
  }

  /** Stackable trait to convert the entryName to Capital-Hyphen-Case.
    */
  trait CapitalHyphencase extends EnumEntry {
    override def entryName: String = stableEntryName

    private[this] lazy val stableEntryName: String = camel2WordArray(super.entryName).mkString("-")
  }

  /** Stackable trait to convert the entryName to Capital.Dot.Case.
    */
  trait CapitalDotcase extends EnumEntry {
    override def entryName: String = stableEntryName

    private[this] lazy val stableEntryName: String = camel2WordArray(super.entryName).mkString(".")
  }

  /** Stackable trait to convert the entryName to Capital Words.
    */
  trait CapitalWords extends EnumEntry {
    override def entryName: String = stableEntryName

    private[this] lazy val stableEntryName: String = camel2WordArray(super.entryName).mkString(" ")
  }

  /** Stackable trait to convert the entryName to CamelCase.
    */
  trait Camelcase extends EnumEntry {
    override def entryName: String = stableEntryName

    private[this] lazy val stableEntryName: String =
      camel2WordArray(super.entryName).map(s => capitalise(s.toLowerCase)).mkString
  }

  /** Stackable trait to convert the entryName to UPPERCASE.
    */
  trait Uppercase extends EnumEntry {
    override def entryName: String = stableEntryName

    private[this] lazy val stableEntryName: String = super.entryName.toUpperCase
  }

  /** Stackable trait to convert the entryName to lowercase.
    */
  trait Lowercase extends EnumEntry {
    override def entryName: String = stableEntryName

    private[this] lazy val stableEntryName: String = super.entryName.toLowerCase
  }

  /** Stackable trait to uncapitalise the first letter of the entryName.
    */
  trait Uncapitalised extends EnumEntry {
    override def entryName: String = stableEntryName

    private[this] lazy val stableEntryName: String = uncapitalise(super.entryName)
  }

  /** Stackable trait to convert the entryName to snake_case.
    */
  trait Snakecase extends EnumEntry with CapitalSnakecase with Lowercase

  /** Stackable trait to convert the entryName to UPPER_SNAKE_CASE
    */
  trait UpperSnakecase extends EnumEntry with CapitalSnakecase with Uppercase

  /** Stackable trait to convert the entryName to hyphen-case.
    */
  trait Hyphencase extends EnumEntry with CapitalHyphencase with Lowercase

  /** Stackable trait to convert the entryName to UPPER-HYPHEN-CASE.
    */
  trait UpperHyphencase extends EnumEntry with CapitalHyphencase with Uppercase

  /** Stackable trait to convert the entryName to dot.case
    */
  trait Dotcase extends EnumEntry with CapitalDotcase with Lowercase

  /** Stackable trait to convert the entryName to UPPER.DOT.CASE
    */
  trait UpperDotcase extends EnumEntry with CapitalDotcase with Uppercase

  /** Stackable trait to convert the entryName to words.
    */
  trait Words extends EnumEntry with CapitalWords with Lowercase

  /** Stackable trait to convert the entryName to UPPER WORDS.
    */
  trait UpperWords extends EnumEntry with CapitalWords with Uppercase

  /** Stackable trait to convert the entryName to lowerCamelCase.
    */
  trait LowerCamelcase extends EnumEntry with Camelcase with Uncapitalised

  /** Helper implicit class that holds enrichment methods
    */
  implicit class EnumEntryOps[A <: EnumEntry](private val enumEntry: A) extends AnyVal {

    /** Checks if the current enum value is contained by the set of enum values in the parameter
      * list.
      *
      * @param firstEntry
      *   First enum of the list.
      * @param otherEnums
      *   Remaining enums.
      * @return
      *   `true` if the current value is contained by the parameter list.
      */
    def in(firstEntry: A, otherEnums: A*): Boolean =
      in(firstEntry +: otherEnums)

    /** Checks if the current enum value is contained by the set of enum values in the parameter
      * list.
      *
      * @param entries
      *   First enum of the list.
      * @return
      *   `true` if the current value is contained by the parameter list.
      */
    def in(entries: Seq[A]): Boolean = entries.contains(enumEntry)
  }

}
