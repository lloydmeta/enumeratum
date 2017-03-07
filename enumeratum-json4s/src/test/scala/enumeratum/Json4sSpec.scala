package enumeratum

import org.json4s.{DefaultFormats, MappingException}
import org.json4s.native.Serialization
import org.scalatest.{FunSpec, Matchers}

class Json4sSpec extends FunSpec with Matchers {

  implicit val formats = DefaultFormats + Json4s.serializer(TrafficLight)

  case class Data(tr: TrafficLight)
  case class DataOpt(tr: Option[TrafficLight])

  describe("to JSON") {
    it("should serialize plain value to entryName") {
      TrafficLight.values.foreach { value =>
        Serialization.write(Data(tr = value)) shouldBe ("""{"tr":"""" + value.entryName + """"}""")
      }
    }

    it("should serialize Some(value) to entryName") {
      TrafficLight.values.foreach { value =>
        Serialization.write(DataOpt(tr = Some(value))) shouldBe ("""{"tr":"""" + value.entryName + """"}""")
      }
    }

    it("should serialize None to nothing") {
      Serialization.write(DataOpt(tr = None)) shouldBe """{}"""
    }
  }

  describe("from JSON") {
    it("should parse enum members when given proper encoding") {
      TrafficLight.values.foreach { value =>
        Serialization.read[Data]("""{"tr":"""" + value.entryName + """"}""").tr shouldBe value
      }
    }

    it("should parse enum members into optional values") {
      TrafficLight.values.foreach { value =>
        Serialization.read[DataOpt]("""{"tr":"""" + value.entryName + """"}""").tr shouldBe Some(
          value)
      }
    }

    it("should parse missing value into None") {
      Serialization.read[DataOpt]("""{}""").tr shouldBe None
    }

    it("should parse invalid value into None") {
      Serialization.read[DataOpt]("""{"tr":"bogus"}""").tr shouldBe None
      Serialization.read[DataOpt]("""{"tr":17}""").tr shouldBe None
      Serialization.read[DataOpt]("""{"tr":true}""").tr shouldBe None
      Serialization.read[DataOpt]("""{"tr":null}""").tr shouldBe None
    }

    it("should fail to parse random JSON values to members") {
      a[MappingException] shouldBe thrownBy(Serialization.read[Data]("""{"tr":"bogus"}"""))
      a[MappingException] shouldBe thrownBy(Serialization.read[Data]("""{"tr":17}"""))
      a[MappingException] shouldBe thrownBy(Serialization.read[Data]("""{"tr":true}"""))
      a[MappingException] shouldBe thrownBy(Serialization.read[Data]("""{"tr":null}"""))
      a[MappingException] shouldBe thrownBy(Serialization.read[Data]("""{}"""))
    }
  }

}
