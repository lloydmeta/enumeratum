package enumeratum.values

import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import org.scalatest.{FunSpec, Matchers}

class JsoniterScalaValueEnumSpec extends FunSpec with Matchers {

  val data = Data(
    JsoniterScalaMediaType.`application/jpeg`,
    JsoniterScalaJsonLibs.JsoniterScala,
    JsoniterScalaDevice.Laptop,
    JsoniterScalaHttpMethod.Put,
    JsoniterScalaBool.Maybe,
    JsoniterScalaDigits.Dos
  )

  implicit val mediaTypeCodec: JsonCodec[JsoniterScalaMediaType] =
    JsoniterScala.codec(JsoniterScalaMediaType)
  implicit val jsonLibsCodec: JsonCodec[JsoniterScalaJsonLibs] =
    JsoniterScala.codec(JsoniterScalaJsonLibs)
  implicit val deviceCodec: JsonCodec[JsoniterScalaDevice] =
    JsoniterScala.codec(JsoniterScalaDevice)
  implicit val httpMethodCodec: JsonCodec[JsoniterScalaHttpMethod] =
    JsoniterScala.codec(JsoniterScalaHttpMethod)
  implicit val boolCodec: JsonCodec[JsoniterScalaBool] = JsoniterScala.codec(JsoniterScalaBool)
  implicit val digitsCodec: JsonCodec[JsoniterScalaDigits] =
    JsoniterScala.codec(JsoniterScalaDigits)

  implicit val dataCodec: JsonValueCodec[Data] = JsonCodecMaker.make[Data](CodecMakerConfig())
  implicit val dataOptCodec: JsonValueCodec[DataOpt] =
    JsonCodecMaker.make[DataOpt](CodecMakerConfig())
  implicit val dataSingleCodec: JsonValueCodec[DataSingle] =
    JsonCodecMaker.make[DataSingle](CodecMakerConfig())
  implicit val mediaTypeMapCodec: JsonValueCodec[Map[JsoniterScalaMediaType, Int]] =
    JsonCodecMaker.make[Map[JsoniterScalaMediaType, Int]](CodecMakerConfig())
  implicit val jsonLibsMapCodec: JsonValueCodec[Map[JsoniterScalaJsonLibs, Int]] =
    JsonCodecMaker.make[Map[JsoniterScalaJsonLibs, Int]](CodecMakerConfig())
  implicit val deviceMapCodec: JsonValueCodec[Map[JsoniterScalaDevice, Int]] =
    JsonCodecMaker.make[Map[JsoniterScalaDevice, Int]](CodecMakerConfig())
  implicit val httpMethodMapCodec: JsonValueCodec[Map[JsoniterScalaHttpMethod, Int]] =
    JsonCodecMaker.make[Map[JsoniterScalaHttpMethod, Int]](CodecMakerConfig())
  implicit val boolMapCodec: JsonValueCodec[Map[JsoniterScalaBool, Int]] =
    JsonCodecMaker.make[Map[JsoniterScalaBool, Int]](CodecMakerConfig())
  implicit val digitsMapCodec: JsonValueCodec[Map[JsoniterScalaDigits, Int]] =
    JsonCodecMaker.make[Map[JsoniterScalaDigits, Int]](CodecMakerConfig())

  describe("to JSON") {
    it("should serialize plain value") {
      read[Data](write(data)) shouldBe data
    }

    it("should serialize Some(value)") {
      new String(write(DataOpt(Some(JsoniterScalaMediaType.`application/jpeg`)))) shouldBe """{"mediaType":3}"""
    }

    it("should serialize None or null to nothing") {
      new String(write(DataOpt(None))) shouldBe """{}"""
      new String(write(DataOpt(null))) shouldBe """{}"""
    }

    it("should serialize values to keys") {
      new String(write(Map[JsoniterScalaMediaType, Int](JsoniterScalaMediaType.`text/json` -> 0))) shouldBe """{"1":0}"""
      new String(write(Map[JsoniterScalaJsonLibs, Int](JsoniterScalaJsonLibs.JsoniterScala -> 0))) shouldBe """{"7":0}"""
      new String(write(Map[JsoniterScalaDevice, Int](JsoniterScalaDevice.Phone             -> 0))) shouldBe """{"1":0}"""
      new String(write(Map[JsoniterScalaHttpMethod, Int](JsoniterScalaHttpMethod.Get       -> 0))) shouldBe """{"GET":0}"""
      new String(write(Map[JsoniterScalaBool, Int](JsoniterScalaBool.True                  -> 0))) shouldBe """{"T":0}"""
      new String(write(Map[JsoniterScalaDigits, Int](JsoniterScalaDigits.Uno               -> 0))) shouldBe """{"1":0}"""
    }
  }

  describe("from JSON") {
    it("should parse enum members when given proper encoding") {
      read[Data](
        """{"mediaType":3,"jsonLib":7,"device":2,"httpMethod":"PUT","bool":"?","digits":2}""".getBytes) shouldBe data
    }

    it("should parse enum members into optional values") {
      read[DataOpt]("""{"mediaType":3}""".getBytes) shouldBe DataOpt(
        Some(JsoniterScalaMediaType.`application/jpeg`))
    }

    it("should parse missing value or null into None") {
      read[DataOpt]("""{}""".getBytes) shouldBe DataOpt(None)
      read[DataOpt]("""{"mediaType":null}""".getBytes) shouldBe DataOpt(None)
    }

    it("should parse enum values to keys") {
      read[Map[JsoniterScalaMediaType, Int]]("""{"1":0}""".getBytes) shouldBe {
        Map[JsoniterScalaMediaType, Int](JsoniterScalaMediaType.`text/json` -> 0)
      }
      read[Map[JsoniterScalaJsonLibs, Int]]("""{"7":0}""".getBytes) shouldBe {
        Map[JsoniterScalaJsonLibs, Int](JsoniterScalaJsonLibs.JsoniterScala -> 0)
      }
      read[Map[JsoniterScalaDevice, Int]]("""{"1":0}""".getBytes) shouldBe {
        Map[JsoniterScalaDevice, Int](JsoniterScalaDevice.Phone -> 0)
      }
      read[Map[JsoniterScalaHttpMethod, Int]]("""{"GET":0}""".getBytes) shouldBe {
        Map[JsoniterScalaHttpMethod, Int](JsoniterScalaHttpMethod.Get -> 0)
      }
      read[Map[JsoniterScalaBool, Int]]("""{"T":0}""".getBytes) shouldBe {
        Map[JsoniterScalaBool, Int](JsoniterScalaBool.True -> 0)
      }
      read[Map[JsoniterScalaDigits, Int]]("""{"1":0}""".getBytes) shouldBe {
        Map[JsoniterScalaDigits, Int](JsoniterScalaDigits.Uno -> 0)
      }
    }

    it("should fail to parse random JSON values to members") {
      a[JsonParseException] shouldBe thrownBy(
        read[DataSingle]("""{"mediaType":"bogus"}""".getBytes))
      a[JsonParseException] shouldBe thrownBy(read[DataSingle]("""{"mediaType":17}""".getBytes))
      a[JsonParseException] shouldBe thrownBy(read[DataSingle]("""{"mediaType":true}""".getBytes))
      a[JsonParseException] shouldBe thrownBy(read[DataSingle]("""{}""".getBytes))
    }
  }
}

case class Data(mediaType: JsoniterScalaMediaType,
                jsonLib: JsoniterScalaJsonLibs,
                device: JsoniterScalaDevice,
                httpMethod: JsoniterScalaHttpMethod,
                bool: JsoniterScalaBool,
                digits: JsoniterScalaDigits)

case class DataOpt(mediaType: Option[JsoniterScalaMediaType])

case class DataSingle(mediaType: JsoniterScalaMediaType)

sealed abstract class JsoniterScalaMediaType(val value: Long, name: String) extends LongEnumEntry
case object JsoniterScalaMediaType extends LongEnum[JsoniterScalaMediaType] {
  case object `text/json`        extends JsoniterScalaMediaType(1L, "text/json")
  case object `text/html`        extends JsoniterScalaMediaType(2L, "text/html")
  case object `application/jpeg` extends JsoniterScalaMediaType(3L, "application/jpeg")

  val values = findValues
}

sealed abstract class JsoniterScalaJsonLibs(val value: Int) extends IntEnumEntry
case object JsoniterScalaJsonLibs extends IntEnum[JsoniterScalaJsonLibs] {
  case object Argonaut      extends JsoniterScalaJsonLibs(1)
  case object Json4s        extends JsoniterScalaJsonLibs(2)
  case object Circe         extends JsoniterScalaJsonLibs(3)
  case object PlayJson      extends JsoniterScalaJsonLibs(4)
  case object SprayJson     extends JsoniterScalaJsonLibs(5)
  case object UPickle       extends JsoniterScalaJsonLibs(6)
  case object JsoniterScala extends JsoniterScalaJsonLibs(7)

  val values = findValues
}

sealed abstract class JsoniterScalaDevice(val value: Short) extends ShortEnumEntry
case object JsoniterScalaDevice extends ShortEnum[JsoniterScalaDevice] {
  case object Phone   extends JsoniterScalaDevice(1)
  case object Laptop  extends JsoniterScalaDevice(2)
  case object Desktop extends JsoniterScalaDevice(3)
  case object Tablet  extends JsoniterScalaDevice(4)

  val values = findValues
}

sealed abstract class JsoniterScalaHttpMethod(val value: String) extends StringEnumEntry
case object JsoniterScalaHttpMethod extends StringEnum[JsoniterScalaHttpMethod] {
  case object Get  extends JsoniterScalaHttpMethod("GET")
  case object Put  extends JsoniterScalaHttpMethod("PUT")
  case object Post extends JsoniterScalaHttpMethod("POST")

  val values = findValues
}

sealed abstract class JsoniterScalaBool(val value: Char) extends CharEnumEntry
case object JsoniterScalaBool extends CharEnum[JsoniterScalaBool] {
  case object True  extends JsoniterScalaBool('T')
  case object False extends JsoniterScalaBool('F')
  case object Maybe extends JsoniterScalaBool('?')

  val values = findValues
}

sealed abstract class JsoniterScalaDigits(val value: Byte) extends ByteEnumEntry
case object JsoniterScalaDigits extends ByteEnum[JsoniterScalaDigits] {
  case object Uno  extends JsoniterScalaDigits(1)
  case object Dos  extends JsoniterScalaDigits(2)
  case object Tres extends JsoniterScalaDigits(3)

  val values = findValues
}
