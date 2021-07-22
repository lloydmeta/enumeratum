package enumeratum.values

import org.scalacheck.{Arbitrary, Gen}

trait ArbitraryInstances {

  implicit def arbByteEnumEntry[EntryType <: ByteEnumEntry: ByteEnum]: Arbitrary[EntryType] =
    arbValueEnumEntry[Byte, EntryType]

  implicit def arbCharEnumEntry[EntryType <: CharEnumEntry: CharEnum]: Arbitrary[EntryType] =
    arbValueEnumEntry[Char, EntryType]

  implicit def arbIntEnumEntry[EntryType <: IntEnumEntry: IntEnum]: Arbitrary[EntryType] =
    arbValueEnumEntry[Int, EntryType]

  implicit def arbLongEnumEntry[EntryType <: LongEnumEntry: LongEnum]: Arbitrary[EntryType] =
    arbValueEnumEntry[Long, EntryType]

  implicit def arbShortEnumEntry[EntryType <: ShortEnumEntry: ShortEnum]: Arbitrary[EntryType] =
    arbValueEnumEntry[Short, EntryType]

  implicit def arbStringEnumEntry[EntryType <: StringEnumEntry: StringEnum]: Arbitrary[EntryType] =
    arbValueEnumEntry[String, EntryType]

  private def arbValueEnumEntry[ValueType, EnumType <: ValueEnumEntry[ValueType]](implicit
      valueEnum: ValueEnum[ValueType, EnumType]
  ): Arbitrary[EnumType] =
    Arbitrary(Gen.oneOf(valueEnum.values))

}
