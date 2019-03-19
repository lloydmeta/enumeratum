package enumeratum
package values

import anorm.{Column, TypeDoesNotMatch}

/**
  * Allows for easy creation of Column[_] instances for use with anorm parsers.
  * Column instances are used when mapping query results back to Scala types.
  * Can be used by importing singleton methods or mixing in the trait.
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
  * scala> import AnormValueEnumColumnSupport._
  * scala> implicit val trafficLightColumn = columnForValueEnum(TrafficLightByInt)
  * }}}
  */
trait AnormValueEnumColumnSupport {
  def columnForValueEnum[ValueType: Column, E <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, E]): Column[E] =
    implicitly[Column[ValueType]].mapResult { value =>
      enum.withValueOpt(value).toRight(TypeDoesNotMatch(s"unsupported value: $value"))
    }
}
object AnormValueEnumColumnSupport extends AnormValueEnumColumnSupport
