package enumeratum

/**
  * Created by Lloyd on 2/4/15.
  */
sealed trait Dummy extends EnumEntry
object Dummy extends Enum[Dummy] with PlayJsonEnum[Dummy] {
  case object A extends Dummy
  case object B extends Dummy
  case object c extends Dummy
  val values = findValues
}

/**
  * Created by dbuschman on 03/20/2018
  */
sealed trait InsensitiveDummy extends EnumEntry
object InsensitiveDummy
    extends Enum[InsensitiveDummy]
    with PlayInsensitiveJsonEnum[InsensitiveDummy] {
  case object A extends InsensitiveDummy
  case object B extends InsensitiveDummy
  case object c extends InsensitiveDummy
  val values = findValues
}
