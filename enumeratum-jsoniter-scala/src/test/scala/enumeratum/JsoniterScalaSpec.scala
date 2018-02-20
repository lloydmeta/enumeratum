package enumeratum

import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import org.scalatest.{FunSpec, Matchers}

class JsoniterScalaSpec extends FunSpec with Matchers {

  case class Data(tr: TrafficLight)
  case class DataOpt(tr: Option[TrafficLight])
  // TODO: uncomment DataMap tests after implementation of the following improvement:
  // https://github.com/plokhotnyuk/jsoniter-scala/issues/14
  // case class DataMap(tr: Map[TrafficLight, Int])

  implicit val trafficLightCodec: JsonCodec[TrafficLight] = JsoniterScala.codec(TrafficLight)
  implicit val dataCodec: JsonCodec[Data]                 = JsonCodecMaker.make[Data](CodecMakerConfig())
  implicit val dataOptCodec: JsonCodec[DataOpt]           = JsonCodecMaker.make[DataOpt](CodecMakerConfig())
  //implicit val dataMapCodec: JsonCodec[DataMap] = JsonCodecMaker.make[DataMap](CodecMakerConfig())

  describe("to JSON") {
    it("should serialize plain value to entryName") {
      TrafficLight.values.foreach { value =>
        new String(write(Data(tr = value))) shouldBe ("""{"tr":"""" + value.entryName + """"}""")
      }
    }

    it("should serialize Some(value) to entryName") {
      TrafficLight.values.foreach { value =>
        new String(write(DataOpt(tr = Some(value)))) shouldBe ("""{"tr":"""" + value.entryName + """"}""")
      }
    }

    it("should serialize None or null to nothing") {
      new String(write(DataOpt(tr = None))) shouldBe """{}"""
      new String(write(DataOpt(tr = null))) shouldBe """{}"""
    }

    /*
    it("should serialize value to key") {
      TrafficLight.values.foreach { value =>
        val name = value.entryName
        new String(write(DataMap(tr = Map(value -> 0)))) shouldBe s"""{"tr":{"$name":0}}"""
      }
    }
   */
  }

  describe("from JSON") {
    it("should parse enum members when given proper encoding") {
      TrafficLight.values.foreach { value =>
        read[Data](("""{"tr":"""" + value.entryName + """"}""").getBytes).tr shouldBe value
      }
      read[Data](("""{"tr":null}""").getBytes).tr shouldBe null
    }

    it("should parse enum members into optional values") {
      TrafficLight.values.foreach { value =>
        read[DataOpt](("""{"tr":"""" + value.entryName + """"}""").getBytes).tr shouldBe Some(value)
      }
    }

    it("should parse missing value or null into None") {
      read[DataOpt]("""{}""".getBytes).tr shouldBe None
      read[DataOpt]("""{"tr":null}""".getBytes).tr shouldBe None
    }

    /*
    it("should parse enum members into keys") {
      TrafficLight.values.foreach { value =>
        val name = value.entryName
        read[DataMap](s"""{"tr":{"$name":0}}""".getBytes).tr shouldBe Map(value -> 0)
      }
    }
     */

    it("should fail to parse random JSON values to members") {
      a[JsonParseException] shouldBe thrownBy(read[Data]("""{"tr":"bogus"}""".getBytes))
      a[JsonParseException] shouldBe thrownBy(read[Data]("""{"tr":17}""".getBytes))
      a[JsonParseException] shouldBe thrownBy(read[Data]("""{"tr":true}""".getBytes))
      a[JsonParseException] shouldBe thrownBy(read[Data]("""{}""".getBytes))
    }
  }
}
