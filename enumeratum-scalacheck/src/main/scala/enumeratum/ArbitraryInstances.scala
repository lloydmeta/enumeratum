package enumeratum

import org.scalacheck.{Arbitrary, Gen}

trait ArbitraryInstances {

  implicit def arbEnumEntry[EnumType <: EnumEntry](
      implicit enum: Enum[EnumType]): Arbitrary[EnumType] = Arbitrary(Gen.oneOf(enum.values))

}
