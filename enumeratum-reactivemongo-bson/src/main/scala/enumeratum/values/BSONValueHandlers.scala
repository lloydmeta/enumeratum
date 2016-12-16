package enumeratum.values

import reactivemongo.bson.{
  BSONHandler,
  BSONInteger,
  BSONLong,
  BSONReader,
  BSONString,
  BSONValue,
  BSONWriter
}

/**
  * Created by Lloyd on 5/3/16.
  *
  * Copyright 2016
  */
/**
  * Holds BSONValue to implicits. The ones that come with ReactiveMongo by default are for subclasses like BSONLong,
  * but what we want are BSONValue and the Reader/Writer/Handler typeclasses are not covariant.
  */
object BSONValueHandlers extends BSONValueReads with BSONValueWrites {

  implicit def anyBsonHandler[A](implicit reader: BSONReader[BSONValue, A],
                                 writer: BSONWriter[A, BSONValue]) =
    new BSONHandler[BSONValue, A] {
      def write(t: A): BSONValue   = writer.write(t)
      def read(bson: BSONValue): A = reader.read(bson)
    }

}

trait BSONValueReads {

  implicit val bsonReaderShort = new BSONReader[BSONValue, Short] {
    def read(bson: BSONValue): Short = bson match {
      case BSONInteger(x) if Short.MaxValue >= x && Short.MinValue <= x =>
        x.toShort
      case _ => throw new RuntimeException(s"Could not convert $bson to Short")
    }
  }

  implicit val bsonReaderInt = new BSONReader[BSONValue, Int] {
    def read(bson: BSONValue): Int = bson match {
      case BSONInteger(x) => x
      case _              => throw new RuntimeException(s"Could not convert $bson to Int")
    }
  }

  implicit val bsonReaderLong = new BSONReader[BSONValue, Long] {
    def read(bson: BSONValue): Long = bson match {
      case BSONLong(x) => x
      case _           => throw new RuntimeException(s"Could not convert $bson to Long")
    }
  }

  implicit val bsonReaderString = new BSONReader[BSONValue, String] {
    def read(bson: BSONValue): String = bson match {
      case BSONString(x) => x
      case _ =>
        throw new RuntimeException(s"Could not convert $bson to String")
    }
  }

  implicit val bsonReaderChar = new BSONReader[BSONValue, Char] {
    def read(bson: BSONValue): Char = bson match {
      case BSONString(x) if x.length == 1 => x.charAt(0)
      case _                              => throw new RuntimeException(s"Could not convert $bson to Char")
    }
  }

  implicit val bsonReaderByte = new BSONReader[BSONValue, Byte] {
    def read(bson: BSONValue): Byte = bson match {
      case BSONInteger(x) => x.toByte
      case _              => throw new RuntimeException(s"Could not convert $bson to Byte")
    }
  }

}

trait BSONValueWrites {

  implicit val bsonWriterShort = new BSONWriter[Short, BSONValue] {
    def write(t: Short): BSONValue = BSONInteger(t.toInt)
  }

  implicit val bsonWriterInt = new BSONWriter[Int, BSONValue] {
    def write(t: Int): BSONValue = BSONInteger(t)
  }

  implicit val bsonWriterLong = new BSONWriter[Long, BSONValue] {
    def write(t: Long): BSONValue = BSONLong(t)
  }

  implicit val bsonWriterString = new BSONWriter[String, BSONValue] {
    def write(t: String): BSONValue = BSONString(t)
  }

  implicit val bsonWriterChar = new BSONWriter[Char, BSONValue] {
    def write(t: Char): BSONValue = BSONString(s"$t")
  }

  implicit val bsonWriterByte = new BSONWriter[Byte, BSONValue] {
    def write(t: Byte): BSONValue = BSONInteger(t.toInt)
  }

}
