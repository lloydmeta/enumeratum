package enumeratum

import org.scalatest.{ Matchers, FunSpec }
import play.api.data.Form
import play.api.libs.json.{ JsNumber, JsString, Json => PlayJson }
import org.scalatest.OptionValues._
import org.scalatest.EitherValues._

class PlayJsonEnumSpec extends FunSpec with Matchers {

  describe("JSON serdes") {

    describe("deserialisation") {

      it("should work with valid values") {
        JsString("A").asOpt[PlayJsonDummy].value shouldBe PlayJsonDummy.A
      }

      it("should fail with invalid values") {
        JsString("D").asOpt[PlayJsonDummy] shouldBe None
        JsNumber(2).asOpt[PlayJsonDummy] shouldBe None
      }
    }

    describe("serialisation") {

      it("should serialise values to JsString") {
        PlayJson.toJson(PlayJsonDummy.A) shouldBe (JsString("A"))
      }

    }

  }
  
}
