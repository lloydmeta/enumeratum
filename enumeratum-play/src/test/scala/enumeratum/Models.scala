package enumeratum

/**
 * Created by Lloyd on 2/4/15.
 */
sealed trait PlayDummyNormal extends EnumEntry

object PlayDummyNormal extends PlayEnum[PlayDummyNormal] {
  case object A extends PlayDummyNormal
  case object B extends PlayDummyNormal
  case object c extends PlayDummyNormal
  val values = findValues
}

sealed trait PlayDummyLowerOnly extends EnumEntry

object PlayDummyLowerOnly extends PlayLowercaseEnum[PlayDummyLowerOnly] {
  case object A extends PlayDummyLowerOnly
  case object B extends PlayDummyLowerOnly
  case object c extends PlayDummyLowerOnly
  val values = findValues
}

sealed trait PlayDummyUpperOnly extends EnumEntry

object PlayDummyUpperOnly extends PlayUppercaseEnum[PlayDummyUpperOnly] {
  case object A extends PlayDummyUpperOnly
  case object B extends PlayDummyUpperOnly
  case object c extends PlayDummyUpperOnly
  val values = findValues
}

