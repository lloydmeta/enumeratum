package enumeratum.values

import com.github.plokhotnyuk.jsoniter_scala.core._

/**
  * Holds overloaded jsoniter-scala codecs for various Enumeratum ValueEnums
  *
  * {{{
  * scala> import enumeratum.values._
  * scala> import com.github.plokhotnyuk.jsoniter_scala.macros._
  * scala> import com.github.plokhotnyuk.jsoniter_scala.core._
  * scala> import scala.collection.immutable.IndexedSeq
  *
  * scala> sealed abstract class ShirtSize(val value:Int) extends IntEnumEntry
  * scala> case object ShirtSize extends IntEnum[ShirtSize] {
  *      |  case object Small  extends ShirtSize(1)
  *      |  case object Medium extends ShirtSize(2)
  *      |  case object Large  extends ShirtSize(3)
  *      |  val values = findValues
  *      | }
  *
  * scala> implicit val shirtEnumCodec: JsonCodec[ShirtSize] = JsoniterScala.codec(ShirtSize)
  * scala> implicit val codec = JsonCodecMaker.make[IndexedSeq[ShirtSize]](CodecMakerConfig())
  * scala> new String(write(ShirtSize.values))
  * res0: String = [1,2,3]
  * }}}
  */
@SuppressWarnings(Array("org.wartremover.warts.Null", "org.wartremover.warts.AsInstanceOf"))
object JsoniterScala {

  def codec[A <: IntEnumEntry: Manifest](enum: IntEnum[A]): JsonCodec[A] =
    new JsonCodec[A] {
      def nullValue: A = null.asInstanceOf[A]

      def decode(in: JsonReader, default: A): A =
        if (in.isNextToken('n')) {
          in.rollbackToken()
          in.readNullOrError(default, "expected number value or null")
        } else {
          in.rollbackToken()
          val v = in.readInt()
          enum.withValueOpt(v).getOrElse(in.enumValueError(v.toString))
        }

      def decodeKey(in: JsonReader): A = {
        val v = in.readKeyAsInt()
        enum.withValueOpt(v).getOrElse(in.enumValueError(v.toString))
      }

      def encode(x: A, out: JsonWriter): Unit =
        if (x ne null) out.writeVal(x.value.toInt) else out.writeNull()

      def encodeKey(x: A, out: JsonWriter): Unit = out.writeKey(x.value.toInt)
    }

  def codec[A <: LongEnumEntry: Manifest](enum: LongEnum[A]): JsonCodec[A] =
    new JsonCodec[A] {
      def nullValue: A = null.asInstanceOf[A]

      def decode(in: JsonReader, default: A): A =
        if (in.isNextToken('n')) {
          in.rollbackToken()
          in.readNullOrError(default, "expected number value or null")
        } else {
          in.rollbackToken()
          val v = in.readLong()
          enum.withValueOpt(v).getOrElse(in.enumValueError(v.toString))
        }

      def decodeKey(in: JsonReader): A = {
        val v = in.readKeyAsLong()
        enum.withValueOpt(v).getOrElse(in.enumValueError(v.toString))
      }

      def encode(x: A, out: JsonWriter): Unit =
        if (x ne null) out.writeVal(x.value.toLong) else out.writeNull()

      def encodeKey(x: A, out: JsonWriter): Unit = out.writeKey(x.value.toLong)
    }

  def codec[A <: ShortEnumEntry: Manifest](enum: ShortEnum[A]): JsonCodec[A] =
    new JsonCodec[A] {
      def nullValue: A = null.asInstanceOf[A]

      def decode(in: JsonReader, default: A): A =
        if (in.isNextToken('n')) {
          in.rollbackToken()
          in.readNullOrError(default, "expected number value or null")
        } else {
          in.rollbackToken()
          val v = in.readShort()
          enum.withValueOpt(v).getOrElse(in.enumValueError(v.toString))
        }

      def decodeKey(in: JsonReader): A = {
        val v = in.readKeyAsShort()
        enum.withValueOpt(v).getOrElse(in.enumValueError(v.toString))
      }

      def encode(x: A, out: JsonWriter): Unit =
        if (x ne null) out.writeVal(x.value.toShort) else out.writeNull()

      def encodeKey(x: A, out: JsonWriter): Unit = out.writeKey(x.value.toShort)
    }

  def codec[A <: StringEnumEntry: Manifest](enum: StringEnum[A]): JsonCodec[A] =
    new JsonCodec[A] {
      def nullValue: A = null.asInstanceOf[A]

      def decode(in: JsonReader, default: A): A =
        if (in.isNextToken('n')) {
          in.rollbackToken()
          in.readNullOrError(default, "expected string value or null")
        } else {
          in.rollbackToken()
          val v = in.readString()
          enum.withValueOpt(v).getOrElse(in.enumValueError(v))
        }

      def decodeKey(in: JsonReader): A = {
        val v = in.readKeyAsString()
        enum.withValueOpt(v).getOrElse(in.enumValueError(v))
      }

      def encode(x: A, out: JsonWriter): Unit =
        if (x ne null) out.writeVal(x.value.toString) else out.writeNull()

      def encodeKey(x: A, out: JsonWriter): Unit = out.writeKey(x.value.toString)
    }

  def codec[A <: ByteEnumEntry: Manifest](enum: ByteEnum[A]): JsonCodec[A] =
    new JsonCodec[A] {
      def nullValue: A = null.asInstanceOf[A]

      def decode(in: JsonReader, default: A): A =
        if (in.isNextToken('n')) {
          in.rollbackToken()
          in.readNullOrError(default, "expected number value or null")
        } else {
          in.rollbackToken()
          val v = in.readByte()
          enum.withValueOpt(v).getOrElse(in.enumValueError(v.toString))
        }

      def decodeKey(in: JsonReader): A = {
        val v = in.readKeyAsByte()
        enum.withValueOpt(v).getOrElse(in.enumValueError(v.toString))
      }

      def encode(x: A, out: JsonWriter): Unit =
        if (x ne null) out.writeVal(x.value.toByte) else out.writeNull()

      def encodeKey(x: A, out: JsonWriter): Unit = out.writeKey(x.value.toByte)
    }

  def codec[A <: CharEnumEntry: Manifest](enum: CharEnum[A]): JsonCodec[A] =
    new JsonCodec[A] {
      def nullValue: A = null.asInstanceOf[A]

      def decode(in: JsonReader, default: A): A =
        if (in.isNextToken('n')) {
          in.rollbackToken()
          in.readNullOrError(default, "expected string value or null")
        } else {
          in.rollbackToken()
          val v = in.readChar()
          enum.withValueOpt(v).getOrElse(in.enumValueError(v.toString))
        }

      def decodeKey(in: JsonReader): A = {
        val v = in.readKeyAsChar()
        enum.withValueOpt(v).getOrElse(in.enumValueError(v.toString))
      }

      def encode(x: A, out: JsonWriter): Unit =
        if (x ne null) out.writeVal(x.value.toString) else out.writeNull()

      def encodeKey(x: A, out: JsonWriter): Unit = out.writeKey(x.value.toChar)
    }
}
