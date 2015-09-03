package enumeratum

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

  /**
   * Stackable trait to convert the entryName to snake_case. For UPPER_SNAKE_CASE,
   * also mix in [[Uppercase]] after this one.
   */
  trait Snakecase extends EnumEntry {
    abstract override def entryName: String = camel2snake(super.entryName)

    private def camel2snake(name: String) =
      "[A-Z]".r.replaceAllIn(name, { m => "_" + m.group(0).toLowerCase }).stripPrefix("_")
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

}
