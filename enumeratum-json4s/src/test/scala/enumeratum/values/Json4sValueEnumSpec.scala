package enumeratum.values

import org.json4s.{DefaultFormats, JObject, MappingException}
import org.json4s.JsonDSL._
import org.json4s.native.Serialization
import org.scalatest.{FunSpec, Matchers}

class Json4sValueEnumSpec extends FunSpec with Matchers {

  implicit val formats = DefaultFormats +
      Json4s.serializer(Json4sMediaType) + Json4s.serializer(Json4sJsonLibs) +
      Json4s.serializer(Json4sDevice) + Json4s.serializer(Json4sHttpMethod) +
      Json4s.serializer(Json4sBool) + Json4s.serializer(Json4sDigits)

  val data = Data(Json4sMediaType.`application/jpeg`,
                  Json4sJsonLibs.Json4s,
                  Json4sDevice.Laptop,
                  Json4sHttpMethod.Put,
                  Json4sBool.Maybe,
                  Json4sDigits.Dos)

  describe("to JSON") {
    it("should serialize plain value") {
      Serialization.read[JObject](Serialization.write(data)) shouldBe
        ("mediaType" -> 3) ~ ("jsonLib" -> 2) ~ ("device" -> 2) ~ ("httpMethod" -> "PUT") ~ ("bool" -> "?") ~ ("digits" -> 2)
    }

    it("should serialize Some(value)") {
      Serialization.write(DataOpt(Some(Json4sMediaType.`application/jpeg`))) shouldBe """{"mediaType":3}"""
    }

    it("should serialize None to nothing") {
      Serialization.write(DataOpt(None)) shouldBe """{}"""
    }

  }

  describe("from JSON") {
    it("should parse enum members when given proper encoding") {
      Serialization.read[Data](
        """{"mediaType":3,"jsonLib":2,"device":2,"httpMethod":"PUT","bool":"?","digits":2}""") shouldBe data
    }

    it("should parse enum members into optional values") {
      Serialization.read[DataOpt]("""{"mediaType":3}""") shouldBe DataOpt(
        Some(Json4sMediaType.`application/jpeg`))
    }

    it("should parse missing value into None") {
      Serialization.read[DataOpt]("""{}""") shouldBe DataOpt(None)
    }

    it("should parse invalid value into None") {
      Serialization.read[DataOpt]("""{"mediaType":"bogus"}""") shouldBe DataOpt(None)
      Serialization.read[DataOpt]("""{"mediaType":17}""") shouldBe DataOpt(None)
      Serialization.read[DataOpt]("""{"mediaType":true}""") shouldBe DataOpt(None)
      Serialization.read[DataOpt]("""{"mediaType":null}""") shouldBe DataOpt(None)
    }

    it("should fail to parse random JSON values to members") {
      a[MappingException] shouldBe thrownBy(
        Serialization.read[DataSingle]("""{"mediaType":"bogus"}"""))
      a[MappingException] shouldBe thrownBy(Serialization.read[DataSingle]("""{"mediaType":17}"""))
      a[MappingException] shouldBe thrownBy(
        Serialization.read[DataSingle]("""{"mediaType":true}"""))
      a[MappingException] shouldBe thrownBy(
        Serialization.read[DataSingle]("""{"mediaType":null}"""))
      a[MappingException] shouldBe thrownBy(Serialization.read[DataSingle]("""{}"""))
    }
  }
}

case class Data(mediaType: Json4sMediaType,
                jsonLib: Json4sJsonLibs,
                device: Json4sDevice,
                httpMethod: Json4sHttpMethod,
                bool: Json4sBool,
                digits: Json4sDigits)

case class DataOpt(mediaType: Option[Json4sMediaType])

case class DataSingle(mediaType: Json4sMediaType)

sealed abstract class Json4sMediaType(val value: Long, name: String) extends LongEnumEntry
case object Json4sMediaType extends LongEnum[Json4sMediaType] {
  case object `text/json`        extends Json4sMediaType(1L, "text/json")
  case object `text/html`        extends Json4sMediaType(2L, "text/html")
  case object `application/jpeg` extends Json4sMediaType(3L, "application/jpeg")

  val values = findValues
}

sealed abstract class Json4sJsonLibs(val value: Int) extends IntEnumEntry
case object Json4sJsonLibs extends IntEnum[Json4sJsonLibs] {
  case object Argonaut  extends Json4sJsonLibs(1)
  case object Json4s    extends Json4sJsonLibs(2)
  case object Circe     extends Json4sJsonLibs(3)
  case object PlayJson  extends Json4sJsonLibs(4)
  case object SprayJson extends Json4sJsonLibs(5)
  case object UPickle   extends Json4sJsonLibs(6)

  val values = findValues
}

sealed abstract class Json4sDevice(val value: Short) extends ShortEnumEntry
case object Json4sDevice extends ShortEnum[Json4sDevice] {
  case object Phone   extends Json4sDevice(1)
  case object Laptop  extends Json4sDevice(2)
  case object Desktop extends Json4sDevice(3)
  case object Tablet  extends Json4sDevice(4)

  val values = findValues
}

sealed abstract class Json4sHttpMethod(val value: String) extends StringEnumEntry
case object Json4sHttpMethod extends StringEnum[Json4sHttpMethod] {
  case object Get  extends Json4sHttpMethod("GET")
  case object Put  extends Json4sHttpMethod("PUT")
  case object Post extends Json4sHttpMethod("POST")

  val values = findValues
}

sealed abstract class Json4sBool(val value: Char) extends CharEnumEntry
case object Json4sBool extends CharEnum[Json4sBool] {
  case object True  extends Json4sBool('T')
  case object False extends Json4sBool('F')
  case object Maybe extends Json4sBool('?')

  val values = findValues
}

sealed abstract class Json4sDigits(val value: Byte) extends ByteEnumEntry
case object Json4sDigits extends ByteEnum[Json4sDigits] {
  case object Uno  extends Json4sDigits(1)
  case object Dos  extends Json4sDigits(2)
  case object Tres extends Json4sDigits(3)

  val values = findValues
}
