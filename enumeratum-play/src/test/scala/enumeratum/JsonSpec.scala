package enumeratum

import org.scalatest.{ Matchers, FunSpec }
import play.api.libs.json.{ JsNumber, JsString }

class JsonSpec extends FunSpec with Matchers {

  sealed trait DummyEnum
  object DummyEnum extends Enum[DummyEnum] {
    case object A extends DummyEnum
    case object B extends DummyEnum
    case object C extends DummyEnum
    val values = findValues
  }

  describe("reads") {
    val reads = Json.reads(DummyEnum)

    it("should create a reads that works with valid values") {
      reads.reads(JsString("A")).get should be(DummyEnum.A)
    }

    it("should create a reads that fails with invalid values") {
      reads.reads(JsString("D")).isError should be(true)
      reads.reads(JsNumber(2)).isError should be(true)
    }
  }

  describe("writes") {
    val writer = Json.writes(DummyEnum)

    it("should create a writes that writes enum values to JsString") {
      writer.writes(DummyEnum.A) should be(JsString("A"))
    }
  }

  describe("formats") {
    val format = Json.formats(DummyEnum)

    it("should create a format that works with valid values") {
      format.reads(JsString("A")).get should be(DummyEnum.A)
    }

    it("should create a format that fails with invalid values") {
      format.reads(JsString("D")).isError should be(true)
      format.reads(JsNumber(2)).isError should be(true)
    }

    it("should create a format that writes enum values to JsString") {
      format.writes(DummyEnum.A) should be(JsString("A"))
    }
  }

}
