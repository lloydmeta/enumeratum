package enumeratum
import java.sql.PreparedStatement

import anorm.ToStatement

/**
  * Allows for easy creation of ToStatement[_] instances for use with anorm queries.
  * ToStatement instances are used when inserting Scala types into interpolated sql queries.
  * Can be used by importing singleton methods or mixing in the trait.
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
  * scala> import AnormEnumToStatementSupport._
  * scala> implicit val trafficLightToStatement = toStatementForEnum(TrafficLight)
  * }}}
  */
trait AnormEnumToStatementSupport {
  def toStatementForEnum[E <: EnumEntry](enum: Enum[E]): ToStatement[E] = {
    /* Implementation note: the enum argument is not used directly, but is used
       for type inference - if it wasn't required the caller would have to pass a type
       parameter, which would make the interface for toStatement different from everything
       else. For consistency at the call site, we just ask for the enum itself as direct argument
     */
    toStatementForEnum(nameFunc = identity)
  }

  def toStatementForEnumUppercase[E <: EnumEntry](enum: Enum[E]): ToStatement[E] =
    toStatementForEnum(_.toUpperCase)

  def toStatementForEnumLowercase[E <: EnumEntry](enum: Enum[E]): ToStatement[E] =
    toStatementForEnum(_.toLowerCase)

  private def toStatementForEnum[E <: EnumEntry](nameFunc: String => String): ToStatement[E] =
    new ToStatement[E] {
      def set(s: PreparedStatement, index: Int, v: E): Unit = {
        val transformedName = nameFunc(v.entryName)
        s.setString(index, transformedName)
      }
    }
}
object AnormEnumToStatementSupport extends AnormEnumToStatementSupport
