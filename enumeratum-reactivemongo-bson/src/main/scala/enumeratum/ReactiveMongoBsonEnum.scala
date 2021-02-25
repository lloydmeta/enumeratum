package enumeratum

import reactivemongo.api.bson.{BSONHandler, KeyReader, KeyWriter}

/**
  * @author Alessandro Lacava (@lambdista)
  * @since 2016-04-23
  */
trait ReactiveMongoBsonEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val bsonHandler: BSONHandler[A] =
    EnumHandler.handler(this)

  implicit val keyReader: KeyReader[A] = EnumHandler.keyReader[A](this)
  implicit val keyWriter: KeyWriter[A] = EnumHandler.keyWriter[A](this)
}
