package enumeratum

import org.scalacheck.{Arbitrary, Gen}

trait ArbitraryInstances {

  implicit def arbEnumEntry[EnumType <: EnumEntry](implicit
      myEnum: Enum[EnumType]
  ): Arbitrary[EnumType] = Arbitrary(Gen.oneOf(myEnum.values))

}
