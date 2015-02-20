package enumeratum

import org.scalatest.{ Matchers, FunSpec }
import play.api.data.Form
import play.api.libs.json.{ JsNumber, JsString, Json => PlayJson }
import org.scalatest.OptionValues._
import org.scalatest.EitherValues._

class PlayEnumSpec extends FunSpec with Matchers {

  describe("JSON serdes") {

    describe("deserialisation") {

      it("should work with valid values") {
        JsString("A").asOpt[PlayDummy].value shouldBe PlayDummy.A
      }

      it("should fail with invalid values") {
        JsString("D").asOpt[PlayDummy] shouldBe None
        JsNumber(2).asOpt[PlayDummy] shouldBe None
      }
    }

    describe("serialisation") {

      it("should serialise values to JsString") {
        PlayJson.toJson(PlayDummy.A) shouldBe (JsString("A"))
      }

    }

  }

  describe("Form binding") {

    val subject = Form("hello" -> PlayDummy.formField)

    it("should bind proper strings into an Enum value") {
      val r1 = subject.bind(Map("hello" -> "A"))
      val r2 = subject.bind(Map("hello" -> "B"))
      r1.value.value shouldBe PlayDummy.A
      r2.value.value shouldBe PlayDummy.B
    }

    it("should fail to bind random strings") {
      val r = subject.bind(Map("hello" -> "AARSE"))
      r.value shouldBe None
    }

  }

  describe("URL binding") {

    describe("PathBindable") {

      val subject = PlayDummy.pathBindable

      it("should bind strings corresponding to enum strings") {
        subject.bind("hello", "A").right.value shouldBe PlayDummy.A
      }

      it("should not bind strings not found in the enumeration") {
        subject.bind("hello", "Z").isLeft shouldBe true
      }

      it("should unbind values") {
        subject.unbind("hello", PlayDummy.A) shouldBe "A"
        subject.unbind("hello", PlayDummy.B) shouldBe "B"
      }

    }

    describe("QueryStringBindable") {

      val subject = PlayDummy.queryBindable

      it("should bind strings corresponding to enum strings regardless of case") {
        subject.bind("hello", Map("hello" -> Seq("A"))).value.right.value should be(PlayDummy.A)
      }

      it("should not bind strings not found in the enumeration") {
        subject.bind("hello", Map("hello" -> Seq("Z"))).value should be('left)
        subject.bind("hello", Map("helloz" -> Seq("A"))) shouldBe None
      }

      it("should unbind values") {
        subject.unbind("hello", PlayDummy.A) should be("hello=A")
        subject.unbind("hello", PlayDummy.B) should be("hello=B")
      }

    }

  }

}
