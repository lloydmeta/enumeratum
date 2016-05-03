package enumeratum

import reactivemongo.bson._

import scala.util.{ Failure, Success }

/**
 * Holds BSON reader and writer for [[enumeratum.Enum]]
 *
 * @author Alessandro Lacava (@lambdista)
 * @since 2016-04-23
 */
object EnumHandler {
  /**
   * Returns a BSONReader for a given enum [[Enum]]
   *
   * @param enum The enum
   * @param insensitive bind in a case-insensitive way, defaults to false
   */
  def reader[A <: EnumEntry](enum: Enum[A], insensitive: Boolean = false): BSONReader[BSONValue, A] =
    new BSONReader[BSONValue, A] {
      override def read(bson: BSONValue): A = {
        val result = bson match {
          case BSONString(s) => {
            val maybeBound = if (insensitive) enum.withNameInsensitiveOption(s) else enum.withNameOption(s)
            maybeBound match {
              case Some(obj) => Success(obj)
              case None => Failure(new RuntimeException(
                s"Enumeration expected of type: '$enum', but it does not appear to contain the value: '$s'"
              ))
            }
          }
          case _ => Failure(new RuntimeException("String value expected"))
        }

        result.get
      }
    }

  /**
   * Returns a BSONWriter for a given enum [[Enum]]
   */
  def writer[A <: EnumEntry](enum: Enum[A]): BSONWriter[A, BSONValue] = new BSONWriter[A, BSONValue] {
    override def write(t: A): BSONValue = BSONString(t.entryName)
  }

  /**
   * Returns a BSONHandler for a given enum [[Enum]]
   *
   * @param enum The enum
   * @param insensitive bind in a case-insensitive way, defaults to false
   */
  def handler[A <: EnumEntry](enum: Enum[A], insensitive: Boolean = false): BSONHandler[BSONValue, A] =
    new BSONHandler[BSONValue, A] {
      val concreteReader = reader(enum, insensitive)
      val concreteWriter = writer(enum)

      override def read(bson: BSONValue): A = concreteReader.read(bson)

      override def write(t: A): BSONValue = concreteWriter.write(t)
    }
}
