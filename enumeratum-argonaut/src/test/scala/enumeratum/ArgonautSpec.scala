package enumeratum

import org.scalatest.{FunSpec, Matchers}

import argonaut._
import Argonaut._

/** Created by alonsodomin on 14/10/2016.
  */
class ArgonautSpec extends FunSpec with Matchers {

  describe("to JSON") {
    it("should work") {
      TrafficLight.values.foreach { value =>
        value.asJson shouldBe value.entryName.asJson
      }
    }

    it("should work for lower case") {
      TrafficLight.values.foreach { value =>
        value.asJson(
          Argonauter.encoderLowercase(TrafficLight)
        ) shouldBe value.entryName.toLowerCase.asJson
      }
    }

    it("should work for upper case") {
      TrafficLight.values.foreach { value =>
        value.asJson(
          Argonauter.encoderUppercase(TrafficLight)
        ) shouldBe value.entryName.toUpperCase.asJson
      }
    }
  }

  describe("from JSON") {
    it("should parse enum members when given proper encoding") {
      TrafficLight.values.foreach { value =>
        value.entryName.asJson.as[TrafficLight] shouldBe okResult(value)
      }
    }

    it("should parse enum members when given proper encoding for lower case") {
      TrafficLight.values.foreach { value =>
        value.entryName.toLowerCase.asJson
          .as[TrafficLight](Argonauter.decoderLowercaseOnly(TrafficLight)) shouldBe okResult(value)
      }
    }

    it("should parse enum members when given proper encoding for upper case") {
      TrafficLight.values.foreach { value =>
        value.entryName.toUpperCase.asJson
          .as[TrafficLight](Argonauter.decoderUppercaseOnly(TrafficLight)) shouldBe okResult(value)
      }
    }

    it("should fail to parse random JSON values to members") {
      val results = Seq("XXL".asJson, Int.MaxValue.asJson).map(_.as[TrafficLight])
      results.foreach { res =>
        res.result.isLeft shouldBe true
        res.history.map(_.toList) shouldBe Some(Nil)
      }
    }
  }

}
