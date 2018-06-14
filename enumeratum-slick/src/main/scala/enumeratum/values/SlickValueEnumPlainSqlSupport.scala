package enumeratum.values

import slick.jdbc._

/**
  * Allows for easy creation of GetResult[_] and SetParameter[_] instances for use with Slick's
  * plain sql functionality.
  * Does not require a profile to be in scope; only works with JDBC
  * GetResult instances are used when mapping query results back to Scala types.
  * SetParameter instances are used when inserting Scala types into interpolated sql queries.
  * {{{
  * scala> import enumeratum.values._
  *
  * scala> sealed abstract class TrafficLightByInt(val value: Int) extends IntEnumEntry
  * scala> object TrafficLightByInt extends IntEnum[TrafficLightByInt] {
  *      |   case object Red    extends TrafficLightByInt(0)
  *      |   case object Yellow extends TrafficLightByInt(1)
  *      |   case object Green  extends TrafficLightByInt(2)
  *      |   val values = findValues
  *      | }
  * scala> import SlickValueEnumPlainSqlSupport._
  * scala> implicit val trafficLightSetParameter = setParameterForIntEnum(TrafficLightByInt)
  * scala> implicit val trafficLightOptionalSetParameter = optionalSetParameterForIntEnum(TrafficLightByInt)
  * scala> implicit val trafficLightGetResult = getResultForIntEnum(TrafficLightByInt)
  * scala> implicit val trafficLightOptionalGetResult = optionalGetResultForIntEnum(TrafficLightByInt)
  * }}}
  */
trait SlickValueEnumPlainSqlSupport {

  private def _makeSetParameter[V, E <: ValueEnumEntry[V]](
      set: (PositionedParameters, V) => Unit
  ): SetParameter[E] = {
    new SetParameter[E] {
      override def apply(v: E, pp: PositionedParameters): Unit = {
        set(pp, v.value)
      }
    }
  }

  private def _makeOptionalSetParameter[V, E <: ValueEnumEntry[V]](
      set: (PositionedParameters, Option[V]) => Unit
  ): SetParameter[Option[E]] = {
    new SetParameter[Option[E]] {
      override def apply(v: Option[E], pp: PositionedParameters): Unit = {
        set(pp, v.map(_.value))
      }
    }
  }

  def setParameterForIntEnum[E <: IntEnumEntry](
      enum: IntEnum[E]
  ): SetParameter[E] = {
    /* Implementation note: the enum argument is not used directly, but is used
       for type inference - if it wasn't required the caller would have to pass a type
       parameter, which would make the interface for set parameters different from everything
       else. For consistency at the call site, we just ask for the enum itself as direct argument
     */
    _makeSetParameter[Int, E](_.setInt(_))
  }
  def optionalSetParameterForIntEnum[E <: IntEnumEntry](
      enum: IntEnum[E]
  ): SetParameter[Option[E]] = _makeOptionalSetParameter[Int, E](_.setIntOption(_))
  def setParameterForLongEnum[E <: LongEnumEntry](
      enum: LongEnum[E]
  ): SetParameter[E] = _makeSetParameter[Long, E](_.setLong(_))
  def optionalSetParameterForLongEnum[E <: LongEnumEntry](
      enum: LongEnum[E]
  ): SetParameter[Option[E]] = _makeOptionalSetParameter[Long, E](_.setLongOption(_))
  def setParameterForShortEnum[E <: ShortEnumEntry](
      enum: ShortEnum[E]
  ): SetParameter[E] = _makeSetParameter[Short, E](_.setShort(_))
  def optionalSetParameterForShortEnum[E <: ShortEnumEntry](
      enum: ShortEnum[E]
  ): SetParameter[Option[E]] = _makeOptionalSetParameter[Short, E](_.setShortOption(_))
  def setParameterForStringEnum[E <: StringEnumEntry](
      enum: StringEnum[E]
  ): SetParameter[E] = _makeSetParameter[String, E](_.setString(_))
  def optionalSetParameterForStringEnum[E <: StringEnumEntry](
      enum: StringEnum[E]
  ): SetParameter[Option[E]] = _makeOptionalSetParameter[String, E](_.setStringOption(_))
  def setParameterForByteEnum[E <: ByteEnumEntry](
      enum: ByteEnum[E]
  ): SetParameter[E] = _makeSetParameter[Byte, E](_.setByte(_))
  def optionalSetParameterForByteEnum[E <: ByteEnumEntry](
      enum: ByteEnum[E]
  ): SetParameter[Option[E]] = _makeOptionalSetParameter[Byte, E](_.setByteOption(_))
  def setParameterForCharEnum[E <: CharEnumEntry](
      enum: CharEnum[E]
  ): SetParameter[E] =
    _makeSetParameter[Char, E]({ (pp, char) =>
      pp.setString(char.toString)
    })
  def optionalSetParameterForCharEnum[E <: CharEnumEntry](
      enum: CharEnum[E]
  ): SetParameter[Option[E]] =
    _makeOptionalSetParameter[Char, E]({ (pp, char) =>
      pp.setStringOption(char.map(_.toString))
    })

  private def _makeGetResult[V, E <: ValueEnumEntry[V]](
      enum: ValueEnum[V, E],
      get: PositionedResult => V
  ): GetResult[E] = {
    new GetResult[E] {
      override def apply(pr: PositionedResult): E = enum.valuesToEntriesMap(get(pr))
    }
  }

  private def _makeOptionalGetResult[V, E <: ValueEnumEntry[V]](
      enum: ValueEnum[V, E],
      get: PositionedResult => Option[V]
  ): GetResult[Option[E]] = {
    new GetResult[Option[E]] {
      override def apply(pr: PositionedResult): Option[E] = {
        get(pr).map(v => enum.valuesToEntriesMap(v))
      }
    }
  }

  def getResultForIntEnum[E <: IntEnumEntry](
      enum: IntEnum[E]
  ): GetResult[E] = _makeGetResult[Int, E](enum, _.nextInt)
  def optionalGetResultForIntEnum[E <: IntEnumEntry](
      enum: IntEnum[E]
  ): GetResult[Option[E]] = _makeOptionalGetResult[Int, E](enum, _.nextIntOption)
  def getResultForLongEnum[E <: LongEnumEntry](
      enum: LongEnum[E]
  ): GetResult[E] = _makeGetResult[Long, E](enum, _.nextLong)
  def optionalGetResultForLongEnum[E <: LongEnumEntry](
      enum: LongEnum[E]
  ): GetResult[Option[E]] = _makeOptionalGetResult[Long, E](enum, _.nextLongOption)
  def getResultForShortEnum[E <: ShortEnumEntry](
      enum: ShortEnum[E]
  ): GetResult[E] = _makeGetResult[Short, E](enum, { _.nextShort })
  def optionalGetResultForShortEnum[E <: ShortEnumEntry](
      enum: ShortEnum[E]
  ): GetResult[Option[E]] = _makeOptionalGetResult[Short, E](enum, { _.nextShortOption })
  def getResultForStringEnum[E <: StringEnumEntry](
      enum: StringEnum[E]
  ): GetResult[E] = _makeGetResult[String, E](enum, { _.nextString })
  def optionalGetResultForStringEnum[E <: StringEnumEntry](
      enum: StringEnum[E]
  ): GetResult[Option[E]] = _makeOptionalGetResult[String, E](enum, { _.nextStringOption })
  def getResultForByteEnum[E <: ByteEnumEntry](
      enum: ByteEnum[E]
  ): GetResult[E] = _makeGetResult[Byte, E](enum, { _.nextByte })
  def optionalGetResultForByteEnum[E <: ByteEnumEntry](
      enum: ByteEnum[E]
  ): GetResult[Option[E]] = _makeOptionalGetResult[Byte, E](enum, { _.nextByteOption })
  def getResultForCharEnum[E <: CharEnumEntry](
      enum: CharEnum[E]
  ): GetResult[E] =
    _makeGetResult[Char, E](enum, { pr =>
      pr.nextString.head
    })
  def optionalGetResultForCharEnum[E <: CharEnumEntry](
      enum: CharEnum[E]
  ): GetResult[Option[E]] =
    _makeOptionalGetResult[Char, E](enum, { pr =>
      pr.nextStringOption.map(_.head)
    })

}

object SlickValueEnumPlainSqlSupport extends SlickValueEnumPlainSqlSupport
