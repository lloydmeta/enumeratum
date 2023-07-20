package enumeratum.values

private[values] trait ValueEnumSpecCompat { this: ValueEnumSpec =>
  // recursively referential types can't be defined inside a describe block
  abstract class AbstractEnumEntry[A, Comp <: AbstractEnumEntryCompanion[A, ?]](
      override val value: String
  ) extends StringEnumEntry
  abstract class AbstractEnumEntryCompanion[A, Entry <: AbstractEnumEntry[A, ?]](entry: Entry) {
    def thing: A
  }

  def scalaCompat = describe("Scala3 higher-kinded types") {
    it("should work with higher-kinded type parameters") {
      // In scala 2, `extends StringEnum[Entry[?]]` was allowed, and worked fine with findValues.
      // In scala 3, `extends StringEnum[Entry[?]]` is not allowed (it fails with "unreducible application of
      // higher-kinded type Entry to wildcard arguments"). The closest thing is `extends StringEnum[Entry[Any]]`,
      // which would not have worked with findValues in scala 2, but has been made to work in scala 3 as the
      // existential types needed to resolve the type validly have been removed.
      """
      abstract class AbstractEnum[
          Entry[A] <: AbstractEnumEntry[A, Companion[A]],
          Companion[A] <: AbstractEnumEntryCompanion[A, Entry[A]]
      ] extends StringEnum[Entry[Any]]

      sealed abstract class Enum[A](value: String) extends AbstractEnumEntry[A, EnumCompanion[A]](value)
      sealed abstract class EnumCompanion[A](entry: Enum[A]) extends AbstractEnumEntryCompanion[A, Enum[A]](entry)

      object Enum extends AbstractEnum[Enum, EnumCompanion] {
        case class One(thing: Int) extends EnumCompanion[Int](One)
        case object One extends Enum[Int]("One")

        case class Two(thing: String) extends EnumCompanion[String](Two)
        case object Two extends Enum[String]("Two")

        override def values: IndexedSeq[Enum[Any]] = findValues
      }
      """ should compile
    }
  }
}
