package enumeratum

/**
  * Created by Lloyd on 2/4/15.
  */
sealed trait Dummy extends EnumEntry
object Dummy extends Enum[Dummy] with AnormEnum[Dummy] {
  case object A extends Dummy
  case object B extends Dummy
  case object c extends Dummy
  val values = findValues
}

/**
  * Created by dbuschman on 03/20/2018
  */
sealed trait InsensitiveDummy extends EnumEntry
object InsensitiveDummy extends Enum[InsensitiveDummy] with AnormInsensitiveEnum[InsensitiveDummy] {
  case object A extends InsensitiveDummy
  case object B extends InsensitiveDummy
  case object c extends InsensitiveDummy
  val values = findValues
}

sealed trait LowercaseDummy extends EnumEntry
object LowercaseDummy extends Enum[LowercaseDummy] with AnormLowercaseEnum[LowercaseDummy] {
  case object Apple  extends LowercaseDummy
  case object Banana extends LowercaseDummy
  case object Cherry extends LowercaseDummy
  val values = findValues
}

sealed trait UppercaseDummy extends EnumEntry
object UppercaseDummy extends Enum[UppercaseDummy] with AnormUppercaseEnum[UppercaseDummy] {
  case object Apple  extends UppercaseDummy
  case object Banana extends UppercaseDummy
  case object Cherry extends UppercaseDummy
  val values = findValues
}
