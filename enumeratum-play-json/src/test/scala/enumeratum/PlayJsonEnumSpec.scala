package enumeratum

import org.scalatest.{Matchers, FunSpec}
import play.api.libs.json.{JsNumber, JsString, Json => PlayJson}
import org.scalatest.OptionValues._

class PlayJsonEnumSpec extends FunSpec with Matchers {

  describe("JSON serdes") {

    describe("sensitive deserialisation") {

      it("should work with valid values") {
        JsString("A").asOpt[Dummy].value shouldBe Dummy.A
      }

      it("should fail with invalid values") {
        JsString("a").asOpt[Dummy] shouldBe None
        JsString("D").asOpt[Dummy] shouldBe None
        JsNumber(2).asOpt[Dummy] shouldBe None
      }
    }

    describe("in-sensitive deserialisation") {

      it("should work with valid values") {
        JsString("A").asOpt[InsensitiveDummy].value shouldBe InsensitiveDummy.A
        JsString("a").asOpt[InsensitiveDummy].value shouldBe InsensitiveDummy.A
      }

      it("should fail with invalid values") {
        JsString("d").asOpt[InsensitiveDummy] shouldBe None
        JsString("D").asOpt[InsensitiveDummy] shouldBe None
        JsNumber(2).asOpt[Dummy] shouldBe None
      }
    }

    describe("serialisation") {

      it("should serialise values to JsString") {
        PlayJson.toJson(Dummy.A) shouldBe JsString("A")
      }

    }

  }

}
