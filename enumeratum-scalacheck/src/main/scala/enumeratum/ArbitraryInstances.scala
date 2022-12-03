package enumeratum

import org.scalacheck.{Arbitrary, Gen}

trait ArbitraryInstances {

  implicit def arbEnumEntry[EnumType <: EnumEntry](implicit
      @deprecatedName(Symbol("enum")) e: Enum[EnumType]
  ): Arbitrary[EnumType] = Arbitrary(Gen.oneOf(e.values))

}
