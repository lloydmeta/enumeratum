package enumeratum

import reactivemongo.api.bson.BSONHandler

/**
  * @author Alessandro Lacava (@lambdista)
  * @since 2016-04-23
  */
trait ReactiveMongoBsonEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val bsonHandler: BSONHandler[A] =
    EnumHandler.handler(this)
}
