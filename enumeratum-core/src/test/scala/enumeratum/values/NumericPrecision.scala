package enumeratum.values

sealed abstract class NumericPrecision(val value: String) extends StringEnumEntry with AllowAlias

object NumericPrecision extends StringEnum[NumericPrecision] {
  case object Integer extends NumericPrecision("integer")
  case object Int     extends NumericPrecision("integer")

  case object Float  extends NumericPrecision("float")
  case object Double extends NumericPrecision("double")

  val values = findValues
}
