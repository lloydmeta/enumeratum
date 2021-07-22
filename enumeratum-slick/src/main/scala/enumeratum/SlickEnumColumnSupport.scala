package enumeratum

import scala.reflect.ClassTag

/** When mixed in, allows creation of Slick mapped column types for enumeratum.Enum instances
  *
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
  * scala> trait TrafficLightMappers extends SlickEnumSupport {
  *     |   implicit val trafficLightColumnType = mappedColumnTypeForEnum(TrafficLight)
  *     |   val trafficLightLowercaseColumnType = mappedColumnTypeForLowercaseEnum(TrafficLight)
  *     |   val trafficLightUppercaseColumnType = mappedColumnTypeForUppercaseEnum(TrafficLight)
  *     | }
  * }}}
  */
trait SlickEnumColumnSupport {

  val profile: slick.profile.RelationalProfile

  def mappedColumnTypeForEnum[E <: EnumEntry](
      enum: Enum[E]
  )(implicit tag: ClassTag[E]): profile.BaseColumnType[E] = {
    /* This import has a purpose - it brings the proper
    implicit profile.BaseColumnType[String] into scope */
    import profile.api._
    profile.MappedColumnType.base[E, String](
      { _.entryName },
      { enum.namesToValuesMap }
    )
  }

  def mappedColumnTypeForLowercaseEnum[E <: EnumEntry](
      enum: Enum[E]
  )(implicit tag: ClassTag[E]): profile.BaseColumnType[E] = {
    /* This import has a purpose - it brings the proper
    implicit profile.BaseColumnType[String] into scope */
    import profile.api._
    profile.MappedColumnType.base[E, String](
      { _.entryName.toLowerCase },
      { enum.lowerCaseNamesToValuesMap }
    )
  }

  def mappedColumnTypeForUppercaseEnum[E <: EnumEntry](
      enum: Enum[E]
  )(implicit tag: ClassTag[E]): profile.BaseColumnType[E] = {
    /* This import has a purpose - it brings the proper
    implicit profile.BaseColumnType[String] into scope */
    import profile.api._
    profile.MappedColumnType.base[E, String](
      { _.entryName.toUpperCase },
      { enum.upperCaseNameValuesToMap }
    )
  }
}
