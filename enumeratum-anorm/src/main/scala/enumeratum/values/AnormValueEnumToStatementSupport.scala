package enumeratum
package values

import java.sql.PreparedStatement

import anorm.ToStatement

/**
  * Allows for easy creation of ToStatement[_] instances for use with anorm queries.
  * ToStatement instances are used when inserting Scala types into interpolated sql queries.
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
  * scala> import AnormValueEnumToStatementSupport._
  * scala> implicit val trafficLightColumn = toStatementForValueEnum(TrafficLightByInt)
  * }}}
  */
trait AnormValueEnumToStatementSupport {
  def toStatementForValueEnum[ValueType: ToStatement, E <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, E]): ToStatement[E] = {
    /* Implementation note: the enum argument is not used directly, but is used
       for type inference - if it wasn't required the caller would have to pass a type
       parameter, which would make the interface for toStatement different from everything
       else. For consistency at the call site, we just ask for the enum itself as direct argument
     */
    new ToStatement[E] {
      def set(s: PreparedStatement, index: Int, v: E): Unit =
        implicitly[ToStatement[ValueType]].set(s, index, v.value)
    }
  }
}
object AnormValueEnumToStatementSupport extends AnormValueEnumToStatementSupport
