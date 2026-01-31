package enumeratum.values

sealed abstract class IntTransformation(transformation: Int => Int)(override val value: String)
    extends StringEnumEntry

object IntTransformation extends StringEnum[IntTransformation] {
  case object Identity  extends IntTransformation(identity)("identity")
  case object Increment extends IntTransformation(_ + 1)("increment")
  case object Decrement extends IntTransformation(_ - 1)("decrement")

  override lazy val values: IndexedSeq[IntTransformation] = findValues
}
