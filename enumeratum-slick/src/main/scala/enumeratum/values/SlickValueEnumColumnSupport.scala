package enumeratum.values

import scala.reflect.ClassTag

/** When mixed in, allows creation of Slick mapped column types for enumeratum.values.ValueEnum
  * instances
  *
  * {{{
  * scala> import enumeratum.values._
  *
  * scala> sealed abstract class TrafficLightByInt(val value: Int) extends IntEnumEntry
  * scala> object TrafficLightByInt extends IntEnum[TrafficLightByInt] {
  *     |   case object Red    extends TrafficLightByInt(0)
  *     |   case object Yellow extends TrafficLightByInt(1)
  *     |   case object Green  extends TrafficLightByInt(2)
  *     |   val values = findValues
  *     | }
  * scala> trait TrafficLightMappers extends SlickValueEnumColumnSupport {
  *     |   import profile.api._
  *     |   implicit val trafficLightColumnType = mappedColumnTypeForValueEnum(TrafficLightByInt)
  *     | }
  * }}}
  */
trait SlickValueEnumColumnSupport {

  val profile: slick.relational.RelationalProfile

  def mappedColumnTypeForValueEnum[V, E <: ValueEnumEntry[V]](
      @deprecatedName(Symbol("enum")) e: ValueEnum[V, E]
  )(implicit
      tag: ClassTag[E],
      valueColumnType: profile.BaseColumnType[V]
  ): profile.BaseColumnType[E] = {
    profile.MappedColumnType.base[E, V](
      { _.value },
      { e.withValue(_) }
    )
  }

}
