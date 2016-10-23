package enumeratum.values

/**
  * Code from @zifeo
  */
trait CustomEnumEntry extends IntEnumEntry {
  val value: Int
  val name: String
}
trait CustomEnum[T <: CustomEnumEntry] extends IntEnum[T] {
  def apply(name: String): T =
    values.find(_.name == name).get
}
trait CustomEnumComparable[T <: CustomEnumEntry] { this: T =>
  def >=(that: T): Boolean =
    this.value >= that.value
}
sealed abstract class CustomEnumPrivateConstructor private (val value: Int, val name: String)
    extends CustomEnumEntry
    with CustomEnumComparable[CustomEnumPrivateConstructor]
object CustomEnumPrivateConstructor extends CustomEnum[CustomEnumPrivateConstructor] {
  val values = findValues
  case object A extends CustomEnumPrivateConstructor(10, "a")
  case object B extends CustomEnumPrivateConstructor(20, "b")
}
