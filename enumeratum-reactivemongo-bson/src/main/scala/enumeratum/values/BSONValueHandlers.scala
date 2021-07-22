package enumeratum.values

import reactivemongo.api.bson.{BSONHandler, BSONInteger, BSONLong, BSONString, BSONValue}

import scala.util.{Failure, Try}

/** Created by Lloyd on 5/3/16.
  *
  * Copyright 2016
  */
/** Holds BSONValue to implicits. The ones that come with ReactiveMongo by default are for
  * subclasses like BSONLong, but what we want are BSONValue and the Reader/Writer/Handler
  * typeclasses are not covariant.
  */
@deprecated("No longer needed", "ReactiveMongo 1.0.0")
object BSONValueHandlers {

  implicit def shortHandler: BSONHandler[Short] = new BSONHandler[Short] {
    override def writeTry(t: Short): Try[BSONValue] = Try(BSONInteger(t.toInt))

    override def readTry(bson: BSONValue): Try[Short] = bson match {
      case BSONInteger(x) if x.abs <= Short.MaxValue => Try(x.toShort)
      case _ => Failure(new RuntimeException(s"Could not convert $bson to Short"))
    }
  }

  implicit def intHandler: BSONHandler[Int] = new BSONHandler[Int] {
    override def writeTry(t: Int): Try[BSONValue] = Try(BSONInteger(t))

    override def readTry(bson: BSONValue): Try[Int] = bson match {
      case BSONInteger(x) => Try(x)
      case _              => Failure(new RuntimeException(s"Could not convert $bson to Int"))
    }
  }

  implicit def longHandler: BSONHandler[Long] = new BSONHandler[Long] {
    override def writeTry(t: Long): Try[BSONValue] = Try(BSONLong(t))

    override def readTry(bson: BSONValue): Try[Long] = bson match {
      case BSONLong(x) => Try(x)
      case _           => Failure(new RuntimeException(s"Could not convert $bson to Long"))
    }
  }

  implicit def stringHandler: BSONHandler[String] = new BSONHandler[String] {
    override def writeTry(t: String): Try[BSONValue] = Try(BSONString(t))

    override def readTry(bson: BSONValue): Try[String] = bson match {
      case BSONString(x) => Try(x)
      case _             => Failure(new RuntimeException(s"Could not convert $bson to String"))
    }
  }

  implicit def charHandler: BSONHandler[Char] = new BSONHandler[Char] {
    override def writeTry(t: Char): Try[BSONValue] = Try(BSONString(s"$t"))

    override def readTry(bson: BSONValue): Try[Char] = bson match {
      case BSONString(x) if x.length == 1 => Try(x.charAt(0))
      case _ => Failure(new RuntimeException(s"Could not convert $bson to Char"))
    }
  }

  implicit def byteHandler: BSONHandler[Byte] = new BSONHandler[Byte] {
    override def writeTry(t: Byte): Try[BSONValue] = Try(BSONInteger(t.toInt))

    override def readTry(bson: BSONValue): Try[Byte] = bson match {
      case BSONInteger(x) if x.abs <= Byte.MaxValue => Try(x.toByte)
      case _ => Failure(new RuntimeException(s"Could not convert $bson to Byte"))
    }
  }
}
