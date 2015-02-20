package enumeratum

import org.scalatest.{ Matchers, FunSpec }
import play.api.libs.json.{ JsNumber, JsString }
import org.scalatest.OptionValues._

class JsonSpec extends FunSpec with Matchers {

  describe("reads") {
    val reads = Json.reads(Dummy)

    it("should create a reads that works with valid values") {
      reads.reads(JsString("A")).asOpt.value should be(Dummy.A)
    }

    it("should create a reads that fails with invalid values") {
      reads.reads(JsString("D")).isError should be(true)
      reads.reads(JsNumber(2)).isError should be(true)
    }
  }

  describe("writes") {
    val writer = Json.writes(Dummy)

    it("should create a writes that writes enum values to JsString") {
      writer.writes(Dummy.A) should be(JsString("A"))
    }
  }

  describe("formats") {
    val format = Json.formats(Dummy)

    it("should create a format that works with valid values") {
      format.reads(JsString("A")).asOpt.value should be(Dummy.A)
    }

    it("should create a format that fails with invalid values") {
      format.reads(JsString("D")).isError should be(true)
      format.reads(JsNumber(2)).isError should be(true)
    }

    it("should create a format that writes enum values to JsString") {
      format.writes(Dummy.A) should be(JsString("A"))
    }
  }

}
