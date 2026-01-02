package enumeratum.values

sealed abstract class Creature(
    override val value: String,
    isAlive: Boolean = true,
    isHostile: Boolean = false,
    diet: List[String] = List.empty
) extends StringEnumEntry

object Creature extends StringEnum[Creature] {
  case object Human   extends Creature("human")
  case object Ghost   extends Creature("ghost", isAlive = false)
  case object Vampire extends Creature("vampire", isHostile = true, diet = List("blood"))

  override lazy val values: IndexedSeq[Creature] = findValues
}
