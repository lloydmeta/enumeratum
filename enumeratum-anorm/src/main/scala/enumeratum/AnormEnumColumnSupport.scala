package enumeratum
import anorm.{Column, TypeDoesNotMatch}

/**
  * Allows for easy creation of Column[_] instances for use with anorm parsers.
  * Column instances are used when mapping query results back to Scala types.
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
  * scala> import AnormEnumColumnSupport._
  * scala> implicit val trafficLightColumn = columnForEnum(TrafficLight)
  * }}}
  */
trait AnormEnumColumnSupport {
  def columnForEnum[E <: EnumEntry](enum: Enum[E]): Column[E] =
    columnForEnum(enum.withNameOption _)

  def columnForEnumInsensitive[E <: EnumEntry](enum: Enum[E]): Column[E] =
    columnForEnum(enum.withNameInsensitiveOption _)

  def columnForEnumUppercase[E <: EnumEntry](enum: Enum[E]): Column[E] =
    columnForEnum(enum.withNameUppercaseOnlyOption _)

  def columnForEnumLowercase[E <: EnumEntry](enum: Enum[E]): Column[E] =
    columnForEnum(enum.withNameLowercaseOnlyOption _)

  private def columnForEnum[E <: EnumEntry](find: String => Option[E]): Column[E] =
    implicitly[Column[String]].mapResult { string =>
      find(string).toRight(TypeDoesNotMatch(s"unsupported value: $string"))
    }
}
object AnormEnumColumnSupport extends AnormEnumColumnSupport
