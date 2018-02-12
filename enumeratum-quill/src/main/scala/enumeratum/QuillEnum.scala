package enumeratum

import io.getquill.MappedEncoding

/**
  * Helper trait that adds implicit Quill encoders and decoders for an [[Enum]]'s members
  *
  * Example:
  *
  * {{{
  * scala> import enumeratum._
  * scala> import io.getquill._
  *
  * scala> sealed trait ShirtSize extends EnumEntry
  * scala> case object ShirtSize extends Enum[ShirtSize] with QuillEnum[ShirtSize] {
  *      |  case object Small  extends ShirtSize
  *      |  case object Medium extends ShirtSize
  *      |  case object Large  extends ShirtSize
  *      |  val values = findValues
  *      | }
  *
  * scala> case class Shirt(size: ShirtSize)
  *
  * scala> val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
  * scala> import ctx._
  *
  * scala> val size: ShirtSize = ShirtSize.Small
  *
  * scala> ctx.run(query[Shirt].insert(_.size -> lift(size))).string
  * res0: String = INSERT INTO Shirt (size) VALUES (?)
  * }}}
  */
trait QuillEnum[A <: EnumEntry] { this: Enum[A] =>

  /**
    * Implicit Encoder for this enum
    */
  implicit lazy val enumEncoder: MappedEncoding[A, String] = Quill.encoder(this)

  /**
    * Implicit Decoder for this enum
    */
  implicit lazy val enumDecoder: MappedEncoding[String, A] = Quill.decoder(this)

}
