package enumeratum.values

import zio.json.{JsonDecoder, JsonEncoder, JsonFieldDecoder, JsonFieldEncoder}

sealed trait ZioJsonValueEnum[ValueType, EntryType <: ValueEnumEntry[ValueType]] {
  this: ValueEnum[ValueType, EntryType] =>

  /** Implicit JsonEncoder for this enum
    */
  implicit def zioJsonEncoder: JsonEncoder[EntryType]

  /** Implicit JsonDecoder for this enum
    */
  implicit def zioJsonDecoder: JsonDecoder[EntryType]
}

/** ZioJsonValueEnum for IntEnumEntry
  *
  * {{{
  * scala> import enumeratum.values._
  * scala> import zio.json._
  *
  * scala> sealed abstract class ShirtSize(val value: Int) extends IntEnumEntry
  * scala> case object ShirtSize extends IntEnum[ShirtSize] with IntZioJsonEnum[ShirtSize] {
  *     |  case object Small  extends ShirtSize(1)
  *     |  case object Medium extends ShirtSize(2)
  *     |  case object Large  extends ShirtSize(3)
  *     |  val values = findValues
  *     | }
  *
  * scala> val size: ShirtSize = ShirtSize.Small
  *
  * scala> size.toJson
  * res0: String = 1
  *
  * scala> "3".fromJson[ShirtSize]
  * res1: Either[String, ShirtSize] = Right(Large)
  *
  * scala> "10".fromJson[ShirtSize]
  * res2: Either[String, ShirtSize] = Left(10 is not a member of enum ShirtSize)
  * }}}
  */
trait IntZioJsonEnum[EntryType <: IntEnumEntry] extends ZioJsonValueEnum[Int, EntryType] {
  this: ValueEnum[Int, EntryType] =>
  implicit val zioJsonEncoder: JsonEncoder[EntryType]         = ZioJson.encoder(this)
  implicit val zioJsonDecoder: JsonDecoder[EntryType]         = ZioJson.decoder(this)
  implicit val zioJsonKeyEncoder: JsonFieldEncoder[EntryType] = ZioJson.keyEncoder(this)
  implicit val zioJsonKeyDecoder: JsonFieldDecoder[EntryType] = ZioJson.keyDecoder(this)
}

/** ZioJsonValueEnum for LongEnumEntry
  */
trait LongZioJsonEnum[EntryType <: LongEnumEntry] extends ZioJsonValueEnum[Long, EntryType] {
  this: ValueEnum[Long, EntryType] =>
  implicit val zioJsonEncoder: JsonEncoder[EntryType]         = ZioJson.encoder(this)
  implicit val zioJsonDecoder: JsonDecoder[EntryType]         = ZioJson.decoder(this)
  implicit val zioJsonKeyEncoder: JsonFieldEncoder[EntryType] = ZioJson.keyEncoder(this)
  implicit val zioJsonKeyDecoder: JsonFieldDecoder[EntryType] = ZioJson.keyDecoder(this)
}

/** ZioJsonValueEnum for ShortEnumEntry
  */
trait ShortZioJsonEnum[EntryType <: ShortEnumEntry] extends ZioJsonValueEnum[Short, EntryType] {
  this: ValueEnum[Short, EntryType] =>
  implicit val zioJsonEncoder: JsonEncoder[EntryType] = ZioJson.encoder(this)
  implicit val zioJsonDecoder: JsonDecoder[EntryType] = ZioJson.decoder(this)
}

/** ZioJsonValueEnum for StringEnumEntry
  */
trait StringZioJsonEnum[EntryType <: StringEnumEntry] extends ZioJsonValueEnum[String, EntryType] {
  this: ValueEnum[String, EntryType] =>
  implicit val zioJsonEncoder: JsonEncoder[EntryType]         = ZioJson.encoder(this)
  implicit val zioJsonDecoder: JsonDecoder[EntryType]         = ZioJson.decoder(this)
  implicit val zioJsonKeyEncoder: JsonFieldEncoder[EntryType] = ZioJson.keyEncoder(this)
  implicit val zioJsonKeyDecoder: JsonFieldDecoder[EntryType] = ZioJson.keyDecoder(this)
}

/** ZioJsonValueEnum for CharEnumEntry
  */
trait CharZioJsonEnum[EntryType <: CharEnumEntry] extends ZioJsonValueEnum[Char, EntryType] {
  this: ValueEnum[Char, EntryType] =>
  implicit val zioJsonEncoder: JsonEncoder[EntryType] = ZioJson.encoder(this)
  implicit val zioJsonDecoder: JsonDecoder[EntryType] = ZioJson.decoder(this)
}

/** ZioJsonValueEnum for ByteEnumEntry
  */
trait ByteZioJsonEnum[EntryType <: ByteEnumEntry] extends ZioJsonValueEnum[Byte, EntryType] {
  this: ValueEnum[Byte, EntryType] =>
  implicit val zioJsonEncoder: JsonEncoder[EntryType] = ZioJson.encoder(this)
  implicit val zioJsonDecoder: JsonDecoder[EntryType] = ZioJson.decoder(this)
}
