package enumeratum

import scala.reflect.ClassTag

import slick.jdbc.{PositionedParameters, SetParameter}

/**
  * When mixed in, allows creation of Slick mapped column types for enumeratum.Enum instances
  *
  * {{{
  * scala> import enumeratum._
  *
  * scala> sealed trait TrafficLight extends EnumEntry
  * scala> object TrafficLight extends Enum[TrafficLight] {
  *      |   case object Red    extends TrafficLight
  *      |   case object Yellow extends TrafficLight
  *      |   case object Green  extends TrafficLight
  *      |   val values = findValues
  *      | }
  * scala> trait TrafficLightMappers extends SlickEnumSupport {
  *      |   implicit val trafficLightColumnType = mappedColumnTypeForEnum(TrafficLight)
  *      | }
  * }}}
  */
trait SlickEnumSupport {

  val profile: slick.relational.RelationalProfile

  private def _setParameterTypeForEnum[E <: EnumEntry](
      nameFn: (String => String) = identity): SetParameter[E] = {
    new SetParameter[E] {
      override def apply(e: E, pp: PositionedParameters): Unit = {
        val transformedName = nameFn(e.entryName)
        pp.setString(transformedName)
      }
    }
  }

  /**
    * Usage:
    * {{{
    *   implicit val trafficLightSetParameter = setParameterForEnum(TrafficLight)
    * }}}
    */
  def setParameterForEnum[E <: EnumEntry](enum: Enum[E]): SetParameter[E] = {
    _setParameterTypeForEnum(identity)
  }

  def setParameterForLowercaseEnum[E <: EnumEntry](enum: Enum[E]): SetParameter[E] = {
    _setParameterTypeForEnum(_.toLowerCase)
  }

  def setParameterForUppercaseEnum[E <: EnumEntry](enum: Enum[E]): SetParameter[E] = {
    _setParameterTypeForEnum(_.toUpperCase)
  }

  def mappedColumnTypeForEnum[E <: EnumEntry](enum: Enum[E])(
      implicit tag: ClassTag[E]): profile.BaseColumnType[E] = {
    import profile.api._
    profile.MappedColumnType.base[E, String](
      { _.entryName },
      { enum.namesToValuesMap }
    )
  }

  def mappedColumnTypeForLowercaseEnum[E <: EnumEntry](enum: Enum[E])(
      implicit tag: ClassTag[E]): profile.BaseColumnType[E] = {
    import profile.api._
    profile.MappedColumnType.base[E, String](
      { _.entryName.toLowerCase },
      { enum.lowerCaseNamesToValuesMap }
    )
  }

  def mappedColumnTypeForUppercaseEnum[E <: EnumEntry](enum: Enum[E])(
      implicit tag: ClassTag[E]): profile.BaseColumnType[E] = {
    import profile.api._
    profile.MappedColumnType.base[E, String](
      { _.entryName.toUpperCase },
      { enum.upperCaseNameValuesToMap }
    )
  }
}
