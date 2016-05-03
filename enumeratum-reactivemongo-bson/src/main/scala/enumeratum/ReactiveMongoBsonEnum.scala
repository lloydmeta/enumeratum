package enumeratum

import reactivemongo.bson._

/**
 * @author Alessandro Lacava (@lambdista)
 * @since 2016-04-23
 */
trait ReactiveMongoBsonEnum[A <: EnumEntry] {
  self: Enum[A] =>
  implicit val bsonHandler: BSONHandler[BSONValue, A] = EnumHandler.handler(this)
}