package enumeratum

sealed trait ZioJsonShirtSize extends EnumEntry with Product with Serializable

case object ZioJsonShirtSize
    extends ZioJsonEnum[ZioJsonShirtSize]
    with ZioJsonKeyEnum[ZioJsonShirtSize]
    with Enum[ZioJsonShirtSize] {

  case object Small  extends ZioJsonShirtSize
  case object Medium extends ZioJsonShirtSize
  case object Large  extends ZioJsonShirtSize

  val values = findValues

}
