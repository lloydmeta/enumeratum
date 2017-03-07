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

  @SuppressWarnings(Array("org.wartremover.warts.ExplicitImplicitTypes")) // False alarm
  implicit def anyBsonHandler[A](implicit reader: BSONReader[BSONValue, A],
                                 writer: BSONWriter[A, BSONValue]): BSONHandler[BSONValue, A] =
    new BSONHandler[BSONValue, A] {
      def write(t: A): BSONValue = writer.write(t)

      def read(bson: BSONValue): A = reader.read(bson)
    }

}

@SuppressWarnings(Array("org.wartremover.warts.Throw"))
trait BSONValueReads {

  implicit val bsonReaderShort: BSONReader[BSONValue, Short] = new BSONReader[BSONValue, Short] {
    def read(bson: BSONValue): Short = bson match {
      case BSONInteger(x) if x.abs <= Short.MaxValue => x.toShort
      case _                                         => throw new RuntimeException(s"Could not convert $bson to Short")
    }
  }

  implicit val bsonReaderInt: BSONReader[BSONValue, Int] = new BSONReader[BSONValue, Int] {
    def read(bson: BSONValue): Int = bson match {
      case BSONInteger(x) => x
      case _              => throw new RuntimeException(s"Could not convert $bson to Int")
    }
  }

  implicit val bsonReaderLong: BSONReader[BSONValue, Long] = new BSONReader[BSONValue, Long] {
    def read(bson: BSONValue): Long = bson match {
      case BSONLong(x) => x
      case _           => throw new RuntimeException(s"Could not convert $bson to Long")
    }
  }

  implicit val bsonReaderString: BSONReader[BSONValue, String] =
    new BSONReader[BSONValue, String] {
      def read(bson: BSONValue): String = bson match {
        case BSONString(x) => x
        case _ =>
          throw new RuntimeException(s"Could not convert $bson to String")
      }
    }

  implicit val bsonReaderChar: BSONReader[BSONValue, Char] = new BSONReader[BSONValue, Char] {
    def read(bson: BSONValue): Char = bson match {
      case BSONString(x) if x.length == 1 => x.charAt(0)
      case _                              => throw new RuntimeException(s"Could not convert $bson to Char")
    }
  }

  implicit val bsonReaderByte: BSONReader[BSONValue, Byte] = new BSONReader[BSONValue, Byte] {
    def read(bson: BSONValue): Byte = bson match {
      case BSONInteger(x) if x.abs <= Byte.MaxValue => x.toByte
      case _                                        => throw new RuntimeException(s"Could not convert $bson to Byte")
    }
  }

}

trait BSONValueWrites {

  implicit val bsonWriterShort: BSONWriter[Short, BSONValue] = new BSONWriter[Short, BSONValue] {
    def write(t: Short): BSONValue = BSONInteger(t.toInt)
  }

  implicit val bsonWriterInt: BSONWriter[Int, BSONValue] = new BSONWriter[Int, BSONValue] {
    def write(t: Int): BSONValue = BSONInteger(t)
  }

  implicit val bsonWriterLong: BSONWriter[Long, BSONValue] = new BSONWriter[Long, BSONValue] {
    def write(t: Long): BSONValue = BSONLong(t)
  }

  implicit val bsonWriterString: BSONWriter[String, BSONValue] =
    new BSONWriter[String, BSONValue] {
      def write(t: String): BSONValue = BSONString(t)
    }

  implicit val bsonWriterChar: BSONWriter[Char, BSONValue] = new BSONWriter[Char, BSONValue] {
    def write(t: Char): BSONValue = BSONString(s"$t")
  }

  implicit val bsonWriterByte: BSONWriter[Byte, BSONValue] = new BSONWriter[Byte, BSONValue] {
    def write(t: Byte): BSONValue = BSONInteger(t.toInt)
  }

}
