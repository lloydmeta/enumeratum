package enumeratum

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsNumber, JsString, JsSuccess, Json => PlayJson, Writes}
import org.scalatest.OptionValues._

class PlayJsonEnumSpec extends AnyFunSpec with Matchers {

  describe("JSON serdes") {

    describe("deserialisation") {

      describe("case sensitive") {
        it("should work with valid values") {
          JsString("A").asOpt[Dummy].value shouldBe Dummy.A
          JsString("c").asOpt[Dummy].value shouldBe Dummy.c

          JsString("apple").asOpt[LowercaseDummy].value shouldBe LowercaseDummy.Apple
          JsString("cherry").asOpt[LowercaseDummy].value shouldBe LowercaseDummy.Cherry

          JsString("APPLE").asOpt[UppercaseDummy].value shouldBe UppercaseDummy.Apple
          JsString("CHERRY").asOpt[UppercaseDummy].value shouldBe UppercaseDummy.Cherry
        }

        it("should fail with invalid values") {
          JsString("a").asOpt[Dummy] shouldBe None
          JsString("C").asOpt[Dummy] shouldBe None
          JsString("D").asOpt[Dummy] shouldBe None
          JsNumber(2).asOpt[Dummy] shouldBe None

          JsString("Apple").asOpt[LowercaseDummy] shouldBe None
          JsString("Cherry").asOpt[LowercaseDummy] shouldBe None

          JsString("apple").asOpt[UppercaseDummy] shouldBe None
          JsString("cherry").asOpt[UppercaseDummy] shouldBe None
        }
      }

      describe("case in-sensitive") {
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

      it("should deserialise from Map keys") {
        PlayJson.obj("A" -> 1).validate[Map[Dummy, Int]] shouldBe JsSuccess(Map(Dummy.A -> 1))
        PlayJson.obj("A" -> 2).validate[Map[InsensitiveDummy, Int]] shouldBe JsSuccess(
          Map(InsensitiveDummy.A -> 2))
        PlayJson.obj("apple" -> 3).validate[Map[LowercaseDummy, Int]] shouldBe JsSuccess(
          Map(LowercaseDummy.Apple -> 3))
        PlayJson.obj("APPLE" -> 4).validate[Map[UppercaseDummy, Int]] shouldBe JsSuccess(
          Map(UppercaseDummy.Apple -> 4))
      }
    }

    describe("serialisation") {
      it("should serialise values to JsString") {
        PlayJson.toJson(Dummy.A) shouldBe JsString("A")
        PlayJson.toJson(InsensitiveDummy.A) shouldBe JsString("A")
        PlayJson.toJson(LowercaseDummy.Apple) shouldBe JsString("apple")
        PlayJson.toJson(UppercaseDummy.Apple) shouldBe JsString("APPLE")
      }

      it("should serialise as Map keys") {
        PlayJson.toJson(Map(Dummy.A -> 1)) shouldBe PlayJson.obj("A" -> 1)

        PlayJson.toJson(Map(InsensitiveDummy.A   -> 2)) shouldBe PlayJson.obj("A"     -> 2)
        PlayJson.toJson(Map(LowercaseDummy.Apple -> 3)) shouldBe PlayJson.obj("apple" -> 3)
        PlayJson.toJson(Map(UppercaseDummy.Apple -> 4)) shouldBe PlayJson.obj("APPLE" -> 4)
      }
    }

  }

}
