package enumeratum.values

sealed abstract class MacroEval(val value: Int) extends IntEnumEntry

case object MacroEval extends IntEnum[MacroEval] {
  import spire.syntax.literals.radix._

  case object Zero   extends MacroEval(x2"0")
  case object One    extends MacroEval(x2"1")
  case object Two    extends MacroEval(x2"10")
  case object Eleven extends MacroEval(11)

  val values = findValues
}
