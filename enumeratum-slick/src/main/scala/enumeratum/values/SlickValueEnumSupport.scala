package enumeratum.values

import scala.reflect.ClassTag

import slick.jdbc.{PositionedParameters, SetParameter}

trait SlickValueEnumSupport {

  val profile: slick.profile.RelationalProfile

  def mappedColumnTypeForValueEnum[V, E <: ValueEnumEntry[V]](
      enum: ValueEnum[V, E]
  )(implicit tag: ClassTag[E],
    valueColumnType: profile.BaseColumnType[V]): profile.BaseColumnType[E] = {
    profile.MappedColumnType.base[E, V](
      { _.value },
      { enum.withValue(_) }
    )
  }

  private def _makeSetParameterForValueEnum[V, E <: ValueEnumEntry[V]](
      enum: ValueEnum[V, E],
      set: (PositionedParameters, V) => Unit
  ): SetParameter[E] = {
    new SetParameter[E] {
      override def apply(v: E, pp: PositionedParameters): Unit = {
        set(pp, v.value)
      }
    }
  }

  def setParameterForIntEnum[E <: IntEnumEntry](
      enum: IntEnum[E]
  ): SetParameter[E] = _makeSetParameterForValueEnum[Int, E](enum, { _.setInt(_) })
  def setParameterForLongEnum[E <: LongEnumEntry](
      enum: LongEnum[E]
  ): SetParameter[E] = _makeSetParameterForValueEnum[Long, E](enum, { _.setLong(_) })
  def setParameterForShortEnum[E <: ShortEnumEntry](
      enum: ShortEnum[E]
  ): SetParameter[E] = _makeSetParameterForValueEnum[Short, E](enum, { _.setShort(_) })
  def setParameterForStringEnum[E <: StringEnumEntry](
      enum: StringEnum[E]
  ): SetParameter[E] = _makeSetParameterForValueEnum[String, E](enum, { _.setString(_) })
  def setParameterForByteEnum[E <: ByteEnumEntry](
      enum: ByteEnum[E]
  ): SetParameter[E] = _makeSetParameterForValueEnum[Byte, E](enum, { _.setByte(_) })
  def setParameterForCharEnum[E <: CharEnumEntry](
      enum: CharEnum[E]
  ): SetParameter[E] =
    _makeSetParameterForValueEnum[Char, E](enum, { (pp, char) =>
      pp.setByte(char.toByte)
    })

}
