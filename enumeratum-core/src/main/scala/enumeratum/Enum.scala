package enumeratum

import scala.collection.immutable._
import scala.language.experimental.macros

/** All the cool kids have their own Enumeration implementation, most of which try to do so in the
  * name of implementing exhaustive pattern matching.
  *
  * This is yet another one.
  *
  * Example:
  *
  * {{{
  * scala> import enumeratum._
  *
  * scala> sealed trait DummyEnum extends EnumEntry
  *
  * scala> object DummyEnum extends Enum[DummyEnum] {
  *     |   val values = findValues
  *     |   case object Hello   extends DummyEnum
  *     |   case object GoodBye extends DummyEnum
  *     |   case object Hi      extends DummyEnum
  *     | }
  *
  * scala> DummyEnum.withNameOption("Hello")
  * res0: Option[DummyEnum] = Some(Hello)
  *
  * scala> DummyEnum.withNameOption("Nope")
  * res1: Option[DummyEnum] = None
  * }}}
  *
  * @tparam A
  *   The sealed trait
  */
trait Enum[A <: EnumEntry] {

  /** Map of [[A]] object names to [[A]] s
    */
  lazy val namesToValuesMap: Map[String, A] =
    values.map(v => v.entryName -> v).toMap ++ extraNamesToValuesMap

  /** Additional list of names which can be mapped to values, for example to allow mapping of legacy
    * values.
    * @return
    *   a Map of names to Values
    */
  def extraNamesToValuesMap: Map[String, A] = Map.empty[String, A]

  /** Map of [[A]] object names in lower case to [[A]] s for case-insensitive comparison
    */
  lazy final val lowerCaseNamesToValuesMap: Map[String, A] =
    namesToValuesMap.map { case (k, v) => k.toLowerCase -> v }

  /** Map of [[A]] object names in upper case to [[A]] s for case-insensitive comparison
    */
  lazy final val upperCaseNameValuesToMap: Map[String, A] =
    namesToValuesMap.map { case (k, v) => k.toUpperCase() -> v }

  /** Map of [[A]] to their index in the values sequence.
    *
    * A performance optimisation so that indexOf can be found in constant time.
    */
  lazy final val valuesToIndex: Map[A, Int] = values.zipWithIndex.toMap

  /** The sequence of values for your [[Enum]]. You will typically want to implement this in your
    * extending class as a `val` so that `withName` and friends are as efficient as possible.
    *
    * Feel free to implement this however you'd like (including messing around with ordering, etc)
    * if that fits your needs better.
    */
  def values: IndexedSeq[A]

  /** Tries to get an [[A]] by the supplied name. The name corresponds to the .name of the case
    * objects implementing [[A]]
    *
    * Like [[Enumeration]] 's `withName`, this method will throw if the name does not match any of
    * the values' .entryName values.
    */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def withName(name: String): A =
    withNameOption(name).getOrElse(throw new NoSuchElementException(buildNotFoundMessage(name)))

  /** Optionally returns an [[A]] for a given name.
    */
  def withNameOption(name: String): Option[A] = namesToValuesMap.get(name)

  /** Returns an [[Right[A]] ] for a given name, or a [[Left[NoSuchMember]] ] if the name does not
    * match any of the values' .entryName values.
    */
  def withNameEither(name: String): Either[NoSuchMember[A], A] =
    namesToValuesMap.get(name).toRight(NoSuchMember(name, values))

  /** Tries to get an [[A]] by the supplied name. The name corresponds to the .name of the case
    * objects implementing [[A]], disregarding case
    *
    * Like [[Enumeration]] 's `withName`, this method will throw if the name does not match any of
    * the values' .entryName values.
    */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def withNameInsensitive(name: String): A =
    withNameInsensitiveOption(name).getOrElse(
      throw new NoSuchElementException(buildNotFoundMessage(name))
    )

  /** Tries to get an [[A]] by the supplied name. The name corresponds to the .name of the case
    * objects implementing [[A]] transformed to upper case
    *
    * Like [[Enumeration]] 's `withName`, this method will throw if the name does not match any of
    * the values' .entryName values.
    */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def withNameUppercaseOnly(name: String): A =
    withNameUppercaseOnlyOption(name).getOrElse(
      throw new NoSuchElementException(buildNotFoundMessage(name))
    )

  /** Tries to get an [[A]] by the supplied name. The name corresponds to the .name of the case
    * objects implementing [[A]] transformed to lower case
    *
    * Like [[Enumeration]] 's `withName`, this method will throw if the name does not match any of
    * the values' .entryName values.
    */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def withNameLowercaseOnly(name: String): A =
    withNameLowercaseOnlyOption(name).getOrElse(
      throw new NoSuchElementException(buildNotFoundMessage(name))
    )

  /** Optionally returns an [[A]] for a given name, disregarding case
    */
  def withNameInsensitiveOption(name: String): Option[A] =
    lowerCaseNamesToValuesMap.get(name.toLowerCase)

  /** Optionally returns an [[A]] for a given name assuming the value is upper case
    */
  def withNameUppercaseOnlyOption(name: String): Option[A] =
    upperCaseNameValuesToMap.get(name)

  /** Optionally returns an [[A]] for a given name assuming the value is lower case
    */
  def withNameLowercaseOnlyOption(name: String): Option[A] =
    lowerCaseNamesToValuesMap.get(name)

  /** Returns an [[Right[A]] ] for a given name, or a [[Left[NoSuchMember]] ] if the name does not
    * match any of the values' .entryName values, disregarding case.
    */
  def withNameInsensitiveEither(name: String): Either[NoSuchMember[A], A] =
    lowerCaseNamesToValuesMap.get(name.toLowerCase).toRight(NoSuchMember(name, values))

  /** Returns an [[Right[A]] ] for a given name, or a [[Left[NoSuchMember]] ] if the name does not
    * match any of the values' .entryName values, disregarding case.
    */
  def withNameUppercaseOnlyEither(name: String): Either[NoSuchMember[A], A] =
    upperCaseNameValuesToMap.get(name).toRight(NoSuchMember(name, values))

  /** Returns an [[Right[A]] ] for a given name, or a [[Left[NoSuchMember]] ] if the name does not
    * match any of the values' .entryName values, disregarding case.
    */
  def withNameLowercaseOnlyEither(name: String): Either[NoSuchMember[A], A] =
    lowerCaseNamesToValuesMap.get(name).toRight(NoSuchMember(name, values))

  /** Returns the index number of the member passed in the values picked up by this enum
    *
    * @param member
    *   the member you want to check the index of
    * @return
    *   the index of the first element of values that is equal (as determined by ==) to member, or
    * -1, if none exists.
    */
  def indexOf(member: A): Int = valuesToIndex.getOrElse(member, -1)

  /** Method that returns a Seq of [[A]] objects that the macro was able to find.
    *
    * You will want to use this in some way to implement your [[values]] method. In fact, if you
    * aren't using this method...why are you even bothering with this lib?
    */
  protected def findValues: IndexedSeq[A] = macro EnumMacros.findValuesImpl[A]

  private def buildNotFoundMessage(notFoundName: String): String = {
    s"$notFoundName is not a member of Enum ($existingEntriesString)"
  }

  private lazy val existingEntriesString =
    values.map(_.entryName).mkString(", ")

}

object Enum {

  /** Finds the Enum companion object for a particular EnumEntry
    */
  implicit def materializeEnum[A <: EnumEntry]: Enum[A] = macro EnumMacros.materializeEnumImpl[A]

}
