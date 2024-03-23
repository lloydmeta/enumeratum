package enumeratum

import slick.jdbc._

/** Allows for easy creation of GetResult[_] and SetParameter[_] instances for use with Slick's
  * plain sql functionality. Does not require a profile to be in scope; only works with JDBC. Can be
  * used by importing singleton methods or mixing in the trait. GetResult instances are used when
  * mapping query results back to Scala types. SetParameter instances are used when inserting Scala
  * types into interpolated sql queries.
  * {{{
  * scala> import enumeratum._
  *
  * scala> sealed trait TrafficLight extends EnumEntry
  * scala> object TrafficLight extends Enum[TrafficLight] {
  *     |   case object Red    extends TrafficLight
  *     |   case object Yellow extends TrafficLight
  *     |   case object Green  extends TrafficLight
  *     |   val values = findValues
  *     | }
  * scala> import SlickEnumPlainSqlSupport._
  * scala> implicit val trafficLightSetParameter = setParameterForEnum(TrafficLight)
  * scala> implicit val trafficLightOptionalSetParameter = optionalSetParameterForEnum(TrafficLight)
  * scala> implicit val trafficLightGetResult = getResultForEnum(TrafficLight)
  * scala> implicit val trafficLightOptionalGetResult = optionalGetResultForEnum(TrafficLight)
  * }}}
  */
trait SlickEnumPlainSqlSupport {

  private def _makeSetParameter[E <: EnumEntry](
      nameFn: (String => String) = identity
  ): SetParameter[E] = {
    new SetParameter[E] {
      override def apply(e: E, pp: PositionedParameters): Unit = {
        val transformedName = nameFn(e.entryName)
        pp.setString(transformedName)
      }
    }
  }

  private def _makeOptionalSetParameter[E <: EnumEntry](
      nameFn: (String => String) = identity
  ): SetParameter[Option[E]] = {
    new SetParameter[Option[E]] {
      override def apply(e: Option[E], pp: PositionedParameters): Unit = {
        val transformedName = e.map(e => nameFn(e.entryName))
        pp.setStringOption(transformedName)
      }
    }
  }

  def setParameterForEnum[E <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[E]
  ): SetParameter[E] = {
    /* Implementation note: the enum argument is not used directly, but is used
       for type inference - if it wasn't required the caller would have to pass a type
       parameter, which would make the interface for set parameters different from everything
       else. For consistency at the call site, we just ask for the enum itself as direct argument
     */
    _makeSetParameter(identity)
  }

  def optionalSetParameterForEnum[E <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[E]
  ): SetParameter[Option[E]] = _makeOptionalSetParameter(identity)

  def setParameterForEnumLowercase[E <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[E]
  ): SetParameter[E] = _makeSetParameter(_.toLowerCase)

  def optionalSetParameterForEnumLowercase[E <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[E]
  ): SetParameter[Option[E]] = _makeOptionalSetParameter(_.toLowerCase)

  def setParameterForEnumUppercase[E <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[E]
  ): SetParameter[E] = _makeSetParameter(_.toUpperCase)

  def optionalSetParameterForEnumUppercase[E <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[E]
  ): SetParameter[Option[E]] = _makeOptionalSetParameter(_.toUpperCase)

  private def _makeGetResult[E <: EnumEntry](
      find: String => E
  ): GetResult[E] = {
    new GetResult[E] {
      override def apply(pr: PositionedResult): E = find(pr.nextString())
    }
  }

  private def _makeOptionalGetResult[E <: EnumEntry](
      find: String => E
  ): GetResult[Option[E]] = {
    new GetResult[Option[E]] {
      override def apply(pr: PositionedResult): Option[E] = {
        pr.nextStringOption().map(find)
      }
    }
  }

  def getResultForEnum[E <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[E]
  ): GetResult[E] = _makeGetResult[E](e.withName(_))

  def optionalGetResultForEnum[E <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[E]
  ): GetResult[Option[E]] = _makeOptionalGetResult(e.withName(_))

  def getResultForEnumLowercase[E <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[E]
  ): GetResult[E] = _makeGetResult(e.withNameLowercaseOnly(_))

  def optionalGetResultForEnumLowercase[E <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[E]
  ): GetResult[Option[E]] = _makeOptionalGetResult(e.withNameLowercaseOnly(_))

  def getResultForEnumUppercase[E <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[E]
  ): GetResult[E] = _makeGetResult(e.withNameUppercaseOnly(_))

  def optionalGetResultForEnumUppercase[E <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[E]
  ): GetResult[Option[E]] = _makeOptionalGetResult(e.withNameUppercaseOnly(_))

}

object SlickEnumPlainSqlSupport extends SlickEnumPlainSqlSupport
