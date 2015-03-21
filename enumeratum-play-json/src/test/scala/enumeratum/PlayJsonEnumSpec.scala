package enumeratum

import org.scalatest.{ Matchers, FunSpec }
import play.api.libs.json.{ JsNumber, JsString, Json => PlayJson }
import org.scalatest.OptionValues._

class PlayJsonEnumSpec extends FunSpec with Matchers {

  describe("JSON serdes") {

    describe("deserialisation") {

      it("should work with valid values") {
        JsString("A").asOpt[Dummy].value shouldBe Dummy.A
      }

      it("should fail with invalid values") {
        JsString("D").asOpt[Dummy] shouldBe None
        JsNumber(2).asOpt[Dummy] shouldBe None
      }
    }

    describe("serialisation") {

      it("should serialise values to JsString") {
        PlayJson.toJson(Dummy.A) shouldBe (JsString("A"))
      }

    }

  }

}