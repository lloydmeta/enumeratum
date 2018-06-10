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
trait SlickEnumSupport extends SlickEnum {

  val profile: slick.profile.RelationalProfile

  import profile.api._

  /**
    * Usage:
    * {{{
    *   implicit val trafficLightSetParameter = setParameterForEnum(TrafficLight)
    * }}}
    */
  def setParameterForEnum[E <: EnumEntry](enum: Enum[E]): SetParameter[E] =
    buildSetParameterTypeForEnum(identity)

  def setParameterForLowercaseEnum[E <: EnumEntry](enum: Enum[E]): SetParameter[E] =
    buildSetParameterTypeForEnum(_.toLowerCase)

  def setParameterForUppercaseEnum[E <: EnumEntry](enum: Enum[E]): SetParameter[E] =
    buildSetParameterTypeForEnum(_.toUpperCase)

  def mappedColumnTypeForEnum[E <: EnumEntry](enum: Enum[E])(
      implicit tag: ClassTag[E]): profile.BaseColumnType[E] =
    buildMappedColumnTypeForEnum(enum = enum,
                                 eToString = _.entryName,
                                 stringToE = enum.namesToValuesMap,
                                 profile = profile)

  def mappedColumnTypeForLowercaseEnum[E <: EnumEntry](enum: Enum[E])(
      implicit tag: ClassTag[E]): profile.BaseColumnType[E] =
    buildMappedColumnTypeForEnum(enum = enum,
                                 eToString = _.entryName.toLowerCase,
                                 stringToE = enum.lowerCaseNamesToValuesMap,
                                 profile = profile)

  def mappedColumnTypeForUppercaseEnum[E <: EnumEntry](enum: Enum[E])(
      implicit tag: ClassTag[E]): profile.BaseColumnType[E] =
    buildMappedColumnTypeForEnum(enum = enum,
                                 eToString = _.entryName.toUpperCase,
                                 stringToE = enum.upperCaseNameValuesToMap,
                                 profile = profile)
}
