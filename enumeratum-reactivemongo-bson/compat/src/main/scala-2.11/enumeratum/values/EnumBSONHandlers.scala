package enumeratum.values

import reactivemongo.bson.{ BSONHandler, BSONInteger, BSONLong, BSONReader, BSONValue, BSONWriter }

/**
 * Created by Lloyd on 5/3/16.
 *
 * Copyright 2016
 */

/**
 * Holds BSONValue to primitive implicits. The ones that come with ReactiveMongo by default are for subclasses like BSONLong,
 * but what we want are BSONValue and the Reader/Writer/Handler typeclasses are not covariant.
 */
object EnumBSONHandlers extends BSONValueReads with BSONValueWrites {

  implicit def bsonHandler[A](implicit reader: BSONReader[BSONValue, A], writer: BSONWriter[A, BSONValue]) = new BSONHandler[BSONValue, A] {
    def write(t: A): BSONValue = writer.write(t)
    def read(bson: BSONValue): A = reader.read(bson)
  }

}

trait BSONValueReads {

  implicit val bsonReaderShort = new BSONReader[BSONValue, Short] {
    override def read(bson: BSONValue): Short = bson match {
      case BSONInteger(x) => x.toShort
      case _ => throw new RuntimeException(s"Could not convert $bson to Short")
    }
  }

  implicit val bsonReaderInt = new BSONReader[BSONValue, Int] {
    override def read(bson: BSONValue): Int = bson match {
      case BSONInteger(x) => x
      case _ => throw new RuntimeException(s"Could not convert $bson to Int")
    }
  }

  implicit val bsonReaderLong = new BSONReader[BSONValue, Long] {
    override def read(bson: BSONValue): Long = bson match {
      case BSONLong(x) => x
      case _ => throw new RuntimeException(s"Could not convert $bson to Long")
    }
  }

}

trait BSONValueWrites {

  implicit val bsonWriterShort = new BSONWriter[Short, BSONValue] {
    override def write(t: Short): BSONValue = BSONInteger(t)
  }

  implicit val bsonWriterInt = new BSONWriter[Int, BSONValue] {
    override def write(t: Int): BSONValue = BSONInteger(t)
  }

  implicit val bsonWriterLong = new BSONWriter[Long, BSONValue] {
    override def write(t: Long): BSONValue = BSONLong(t)
  }

}