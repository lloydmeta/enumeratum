package enumeratum

import org.scalatest.OptionValues._
import org.scalatest.{ FunSpec, Matchers }
import play.api.libs.json.{ JsNumber, JsResult, JsString }

class EnumFormatsSpec extends FunSpec with Matchers {

  describe("reads") {
    val reads = EnumFormats.reads(Dummy)

    it("should create a reads that works with valid values") {
      reads.reads(JsString("A")).asOpt.value should be(Dummy.A)
    }

    it("should create a reads that fails with invalid values") {
      reads.reads(JsString("D")).isError should be(true)
      errorMessages(reads.reads(JsString("D"))) should be(Seq("error.expected.validenumvalue"))
      reads.reads(JsNumber(2)).isError should be(true)
      errorMessages(reads.reads(JsNumber(2))) should be(Seq("error.expected.enumstring"))
    }
  }

  describe("reads insensitive") {
    val reads = EnumFormats.reads(Dummy, true)

    it("should create a reads that works with valid values disregarding case") {
      reads.reads(JsString("A")).asOpt.value should be(Dummy.A)
      reads.reads(JsString("a")).asOpt.value should be(Dummy.A)
    }

    it("should create a reads that fails with invalid values") {
      reads.reads(JsString("D")).isError should be(true)
      errorMessages(reads.reads(JsString("D"))) should be(Seq("error.expected.validenumvalue"))
      reads.reads(JsNumber(2)).isError should be(true)
      errorMessages(reads.reads(JsNumber(2))) should be(Seq("error.expected.enumstring"))
    }
  }

  describe("reads lower case") {
    val reads = EnumFormats.readsLowercaseOnly(Dummy)

    it("should create a reads that works with valid values that are lower case") {
      reads.reads(JsString("a")).asOpt.value should be(Dummy.A)
    }

    it("should create a reads that fails with invalid values") {
      reads.reads(JsString("A")).isError should be(true)
      errorMessages(reads.reads(JsString("A"))) should be(Seq("error.expected.validenumvalue"))
    }
  }

  describe("reads upper case") {
    val reads = EnumFormats.readsUppercaseOnly(Dummy)

    it("should create a reads that works with valid values that are lower case") {
      reads.reads(JsString("A")).asOpt.value should be(Dummy.A)
    }

    it("should create a reads that fails with invalid values") {
      reads.reads(JsString("a")).isError should be(true)
      errorMessages(reads.reads(JsString("a"))) should be(Seq("error.expected.validenumvalue"))
    }
  }

  describe("writes") {
    val writer = EnumFormats.writes(Dummy)

    it("should create a writes that writes enum values to JsString") {
      writer.writes(Dummy.A) should be(JsString("A"))
    }
  }

  describe("writes lower case") {
    val writer = EnumFormats.writesLowercaseOnly(Dummy)

    it("should create a writes that writes enum values to JsString as lower case") {
      writer.writes(Dummy.A) should be(JsString("a"))
    }
  }

  describe("writes upper case") {
    val writer = EnumFormats.writesUppercaseOnly(Dummy)

    it("should create a writes that writes enum values to JsString as upper case") {
      writer.writes(Dummy.A) should be(JsString("A"))
    }
  }

  describe("formats") {
    val format = EnumFormats.formats(Dummy)

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

  def errorMessages(jsResult: JsResult[_]): Seq[String] =
    jsResult.fold(
      _.collect {
        case (path, errors) => errors.map(_.message).mkString
      },
      _ => Seq.empty
    )
}
