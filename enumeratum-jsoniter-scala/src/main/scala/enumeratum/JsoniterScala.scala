package enumeratum

import com.github.plokhotnyuk.jsoniter_scala.core._

@SuppressWarnings(Array("org.wartremover.warts.Null", "org.wartremover.warts.AsInstanceOf"))
object JsoniterScala {

  /**
    * Returns a jsoniter-scala [[Codec]] for the given Enumeratum enum
    *
    * {{{
    * scala> import enumeratum._
    * scala> import com.github.plokhotnyuk.jsoniter_scala.macros._
    * scala> import com.github.plokhotnyuk.jsoniter_scala.core._
    * scala> import scala.collection.immutable.IndexedSeq
    *
    * scala> sealed trait ShirtSize extends EnumEntry
    * scala> case object ShirtSize extends Enum[ShirtSize] {
    *      |  case object Small  extends ShirtSize
    *      |  case object Medium extends ShirtSize
    *      |  case object Large  extends ShirtSize
    *      |  val values = findValues
    *      | }
    *
    * scala> implicit val shirtEnumCodec: JsonCodec[ShirtSize] = JsoniterScala.codec(ShirtSize)
    * scala> implicit val codec = JsonCodecMaker.make[IndexedSeq[ShirtSize]](CodecMakerConfig())
    * scala> new String(write(ShirtSize.values))
    * res0: String = ["Small","Medium","Large"]
    *
    * scala> read[IndexedSeq[ShirtSize]]("""["Small","Medium","Large"]""".getBytes) == ShirtSize.values
    * res1: Boolean = true
    * }}}
    *
    * @param enum the enum you want to generate a jsoniter-scala serialiser for
    */
  def codec[A <: EnumEntry: Manifest](enum: Enum[A]): JsonCodec[A] =
    new JsonCodec[A] {
      def nullValue: A = null.asInstanceOf[A]

      def decode(in: JsonReader, default: A): A = {
        val v = in.readString()
        if (v eq null) nullValue
        else enum.withNameOption(v).getOrElse(in.enumValueError(v))
      }

      def encode(x: A, out: JsonWriter): Unit = out.writeVal(if (x ne null) x.entryName else null)
    }
}
