package enumeratum

/**
 * Base type for an enum entry for [[Enum]]
 *
 * By default, the name method used for serialising and deseralising Enum values uses
 * toString, but feel free to override to fit your needs
 */
trait EnumEntry {

  /**
   * String representation of this Enum Entry.
   *
   * Override in your implementation if needed
   */
  def name: String = toString

}
