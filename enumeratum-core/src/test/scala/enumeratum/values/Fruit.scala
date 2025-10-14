package enumeratum.values

sealed abstract class Fruit(override val value: String) extends StringEnumEntry

object Fruit extends StringEnum[Fruit] {
  case object Apple      extends Fruit("apple")
  case object Strawberry extends Fruit("strawberry") with Berry

  override lazy val values: IndexedSeq[Fruit] = findValues

  implicit val ordering: Ordering[Fruit] = Ordering.by(_.value)
}

sealed trait Berry extends Fruit
