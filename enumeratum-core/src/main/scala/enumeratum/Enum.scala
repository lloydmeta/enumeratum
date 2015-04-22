package enumeratum

import scala.language.experimental.macros
import scala.language.postfixOps

/**
 * All the cool kids have their own Enumeration implementation, most of which try to
 * do so in the name of implementing exhaustive pattern matching.
 *
 * This is yet another one.
 *
 * How to use:
 *
 * {{{
 * sealed trait DummyEnum
 *
 * object DummyEnum extends Enum[DummyEnum] {
 *
 * val values = findValues.toIndexedSeq
 *
 * case object Hello extends DummyEnum
 * case object GoodBye extends DummyEnum
 * case object Hi extends DummyEnum
 *
 * }
 *
 *
 * DummyEnum.values should be(Set(Hello, GoodBye, Hi))
 *
 * DummyEnum.withName("Hello") should be(Hello)
 * }}}
 * @tparam A The sealed trait
 */
trait Enum[A <: EnumEntry] {

  /**
   * The sequence of values for your [[Enum]]. You will typically want
   * to implement this in your extending class as a `val` so that `withName`
   * and friends are as efficient as possible.
   *
   * Feel free to implement this however you'd like (including messing around with ordering, etc) if that
   * fits your needs better.
   */
  def values: Seq[A]

  /**
   * Method that returns a Seq of [[A]] objects that the macro was able to find.
   *
   * You will want to use this in some way to implement your [[values]] method. In fact,
   * if you aren't using this method...why are you even bothering with this lib?
   */
  protected def findValues: Seq[A] = macro EnumMacros.findValuesImpl[A]

  /**
   * Map of [[A]] object names to [[A]]s
   */
  lazy final val namesToValuesMap: Map[String, A] = values map (v => v.entryName -> v) toMap

  /**
   * Map of [[A]] object names in lower case to [[A]]s for case-insensitive comparison
   */
  lazy final val lowerCaseNamesToValuesMap: Map[String, A] = values map (v => v.entryName.toLowerCase -> v) toMap

  /**
   * Optionally returns an [[A]] for a given name.
   */
  def withNameOption(name: String): Option[A] = namesToValuesMap get name

  /**
   * Optionally returns an [[A]] for a given name, disregarding case
   */
  def withNameInsensitiveOption(name: String): Option[A] = lowerCaseNamesToValuesMap get name.toLowerCase

  /**
   * Tries to get an [[A]] by the supplied name. The name corresponds to the .name
   * of the case objects implementing [[A]]
   *
   * Like [[Enumeration]]'s `withName`, this method will throw if the name does not match any of the values'
   * .entryName values.
   */
  def withName(name: String): A =
    withNameOption(name) getOrElse (throw new NoSuchElementException(s"$name is not a member of Enum $this"))

  /**
   * Returns the index number of the member passed in the values picked up by this enum
   *
   * @param member
   * @return the index of the first element of values that is equal (as determined by ==) to member, or -1, if none exists.
   */
  def indexOf(member: A): Int = values.indexOf(member)

}