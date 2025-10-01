package enumeratum.values

/** Test case for https://github.com/lloydmeta/enumeratum/issues/XXX
  *
  * Tests that enum values that mix in a shared trait extending the entry type do not result in
  * duplicate values.
  */
sealed abstract class AnimalWithMammal(val value: String) extends StringEnumEntry

object AnimalWithMammal extends StringEnum[AnimalWithMammal] {
  case object Dog   extends AnimalWithMammal("dog") with Mammal
  case object Human extends AnimalWithMammal("human") with Mammal

  val values = findValues
}

sealed trait Mammal extends AnimalWithMammal
