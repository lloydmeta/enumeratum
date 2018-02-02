package enumeratum.values

import org.scalacheck.Cogen

/**
  * `FooEnum` context bounds are unused but can drastically reduce compilation time.
  * See https://github.com/nrinaudo/kantan.codecs/blob/bb74def19e94ce4f14330100b467c3fc9271068d/enumeratum/core/src/main/scala/kantan/codecs/enumeratum/values/ValueEnumInstances.scala#L82
  */
trait CogenInstances {

  implicit def cogenByteEnumEntry[EnumType <: ByteEnumEntry: ByteEnum]: Cogen[EnumType] =
    cogenEnumEntry[Byte, EnumType]

  implicit def cogenCharEnumEntry[EnumType <: CharEnumEntry: CharEnum]: Cogen[EnumType] =
    cogenEnumEntry[Char, EnumType]

  implicit def cogenIntEnumEntry[EnumType <: IntEnumEntry: IntEnum]: Cogen[EnumType] =
    cogenEnumEntry[Int, EnumType]

  implicit def cogenLongEnumEntry[EnumType <: LongEnumEntry: LongEnum]: Cogen[EnumType] =
    cogenEnumEntry[Long, EnumType]

  implicit def cogenShortEnumEntry[EnumType <: ShortEnumEntry: ShortEnum]: Cogen[EnumType] =
    cogenEnumEntry[Short, EnumType]

  implicit def cogenStringEnumEntry[EnumType <: StringEnumEntry: StringEnum]: Cogen[EnumType] =
    cogenEnumEntry[String, EnumType]

  private def cogenEnumEntry[ValueType: Cogen, EnumType <: ValueEnumEntry[ValueType]]
    : Cogen[EnumType] = Cogen[ValueType].contramap(_.value)

}
