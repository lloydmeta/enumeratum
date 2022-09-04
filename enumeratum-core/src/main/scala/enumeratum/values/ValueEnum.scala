package enumeratum.values

/** Base trait for a Value-based enums.
  *
  * Example:
  *
  * {{{
  * scala> sealed abstract class Greeting(val value: Int) extends IntEnumEntry
  *
  * scala> object Greeting extends IntEnum[Greeting] {
  *     |   val values = findValues
  *     |   case object Hello   extends Greeting(1)
  *     |   case object GoodBye extends Greeting(2)
  *     |   case object Hi      extends Greeting(3)
  *     |   case object Bye     extends Greeting(4)
  *     | }
  *
  * scala> Greeting.withValueOpt(1)
  * res0: Option[Greeting] = Some(Hello)
  *
  * scala> Greeting.withValueOpt(6)
  * res1: Option[Greeting] = None
  * }}}
  */
sealed trait ValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {

  /** Map of [[ValueType]] to [[EntryType]] members
    */
  final lazy val valuesToEntriesMap: Map[ValueType, EntryType] =
    values.map(v => v.value -> v).toMap

  /** The sequence of values for your [[Enum]]. You will typically want to implement this in your
    * extending class as a `val` so that `withValue` and friends are as efficient as possible.
    *
    * Feel free to implement this however you'd like (including messing around with ordering, etc)
    * if that fits your needs better.
    */
  def values: IndexedSeq[EntryType]

  /** Tries to get an [[EntryType]] by the supplied value. The value corresponds to the .value of
    * the case objects implementing [[EntryType]]
    *
    * Like [[Enumeration]] 's `withValue`, this method will throw if the value does not match any of
    * the values' `.value` values.
    */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def withValue(i: ValueType): EntryType =
    withValueOpt(i).getOrElse(throw new NoSuchElementException(buildNotFoundMessage(i)))

  /** Optionally returns an [[EntryType]] for a given value.
    */
  def withValueOpt(i: ValueType): Option[EntryType] = valuesToEntriesMap.get(i)

  /** Returns an [[Right[EntryType]] ] for a given value, or a [[Left[NoSuchMember]] ] if the value
    * does not match any of the values' `.value` values.
    */
  def withValueEither(
      i: ValueType
  ): Either[NoSuchMember[ValueType, ValueEnumEntry[ValueType]], EntryType] =
    valuesToEntriesMap.get(i).toRight(NoSuchMember(i, values))

  private lazy val existingEntriesString = values.map(_.value).mkString(", ")

  private def buildNotFoundMessage(i: ValueType): String = {
    s"${i.toString} is not a member of ValueEnum ($existingEntriesString)"
  }

}

/*
 * For the sake of keeping implementations of ValueEnums constrainted to a subset that we have tested to work relatively well,
 * the following traits are implementations of the sealed trait.
 *
 * There is a bit of repetition in order to supply the findValues method (esp in the comments) because we are using a macro
 * and macro invocations cannot provide implementations for a super class's abstract method
 */

object IntEnum extends IntEnumCompanion

/** Value enum with [[IntEnumEntry]] entries
  */
trait IntEnum[A <: IntEnumEntry] extends ValueEnum[Int, A] with IntEnumCompat[A]

object LongEnum extends LongEnumCompanion

/** Value enum with [[LongEnumEntry]] entries
  */
trait LongEnum[A <: LongEnumEntry] extends ValueEnum[Long, A] with LongEnumCompat[A]

object ShortEnum extends ShortEnumCompanion

/** Value enum with [[ShortEnumEntry]] entries
  */
trait ShortEnum[A <: ShortEnumEntry] extends ValueEnum[Short, A] with ShortEnumCompat[A]

object StringEnum extends StringEnumCompanion

/** Value enum with [[StringEnumEntry]] entries
  *
  * This is similar to [[enumeratum.Enum]], but different in that values must be literal values.
  * This restraint allows us to enforce uniqueness at compile time.
  *
  * Note that uniqueness is only guaranteed if you do not do any runtime string manipulation on
  * values.
  */
trait StringEnum[A <: StringEnumEntry] extends ValueEnum[String, A] with StringEnumCompat[A]

object ByteEnum extends ByteEnumCompanion

/** Value enum with [[ByteEnumEntry]] entries
  *
  * This is similar to [[enumeratum.Enum]], but different in that values must be literal values.
  * This restraint allows us to enforce uniqueness at compile time.
  *
  * Note that uniqueness is only guaranteed if you do not do any runtime string manipulation on
  * values.
  */
trait ByteEnum[A <: ByteEnumEntry] extends ValueEnum[Byte, A] with ByteEnumCompat[A]

object CharEnum extends CharEnumCompanion

/** Value enum with [[CharEnumEntry]] entries
  *
  * This is similar to [[enumeratum.Enum]], but different in that values must be literal values.
  * This restraint allows us to enforce uniqueness at compile time.
  *
  * Note that uniqueness is only guaranteed if you do not do any runtime string manipulation on
  * values.
  */
trait CharEnum[A <: CharEnumEntry] extends ValueEnum[Char, A] with CharEnumCompat[A]
