package enumeratum

import org.scalatest.OptionValues._
import org.scalatest.{FunSpec, Matchers}
import play.api.libs.json._

class EnumFormatsSpec extends FunSpec with Matchers {

  testScenario(
    descriptor = "normal operation",
    reads = EnumFormats.reads(Dummy),
    readSuccessExpectations = Map("A" -> Dummy.A),
    readErrors = Map("C"              -> Seq("error.expected.validenumvalue")),
    writes = EnumFormats.writes(Dummy),
    writeExpectations = Map(Dummy.A -> "A"),
    formats = EnumFormats.formats(Dummy)
  )

  testScenario(
    descriptor = "case insensitive",
    reads = EnumFormats.reads(enum = Dummy, insensitive = true),
    readSuccessExpectations = Map(
      "A" -> Dummy.A,
      "a" -> Dummy.A
    ),
    readErrors = Map.empty,
    writes = EnumFormats.writes(Dummy),
    writeExpectations = Map(Dummy.A -> "A"),
    formats = EnumFormats.formats(enum = Dummy, insensitive = true)
  )

  testScenario(
    descriptor = "lower case transformed",
    reads = EnumFormats.readsLowercaseOnly(Dummy),
    readSuccessExpectations = Map(
      "a" -> Dummy.A
    ),
    readErrors = Map(
      "A" -> Seq("error.expected.validenumvalue")
    ),
    writes = EnumFormats.writesLowercaseOnly(Dummy),
    writeExpectations = Map(Dummy.A -> "a"),
    formats = EnumFormats.formatsLowerCaseOnly(Dummy)
  )

  testScenario(
    descriptor = "upper case transformed",
    reads = EnumFormats.readsUppercaseOnly(Dummy),
    readSuccessExpectations = Map(
      "A" -> Dummy.A,
      "C" -> Dummy.c
    ),
    readErrors = Map(
      "a" -> Seq("error.expected.validenumvalue")
    ),
    writes = EnumFormats.writesUppercaseOnly(Dummy),
    writeExpectations = Map(Dummy.A -> "A"),
    formats = EnumFormats.formatsUppercaseOnly(Dummy)
  )

  // Bunch of shared testing methods

  private def errorMessages(jsResult: JsResult[_]): scala.collection.Seq[String] =
    jsResult.fold(
      _.collect {
        case (path, errors) => errors.map(_.message).mkString
      },
      _ => Seq.empty
    )

  private def testScenario(
      descriptor: String,
      reads: Reads[Dummy],
      readSuccessExpectations: Map[String, Dummy],
      readErrors: Map[String, Seq[String]],
      writes: Writes[Dummy],
      writeExpectations: Map[Dummy, String],
      formats: Format[Dummy]
  ): Unit = describe(descriptor) {
    testReads(reads, readSuccessExpectations, readErrors)
    testWrites(writes, writeExpectations)
    testFormats(formats, readSuccessExpectations, readErrors, writeExpectations)
  }

  /**
    * Shared scenarios for testing Reads
    */
  private def testReads(
      reads: Reads[Dummy],
      expectedSuccesses: Map[String, Dummy],
      expectedErrors: Map[String, Seq[String]]
  ): Unit = describe("Reads") {
    val expectedFails: Map[JsValue, Seq[String]] = {
      val withJsValueKeys = expectedErrors.map { case (k, v) => JsString(k) -> v }
      // Add standard errors
      (withJsValueKeys ++ Map(
        JsNumber(2)   -> Seq("error.expected.enumstring"),
        JsString("D") -> Seq("error.expected.validenumvalue")
      )).toMap
    }

    it("should create a reads that works with valid values") {
      expectedSuccesses.foreach {
        case (name, expected) =>
          reads.reads(JsString(name)).asOpt.value should be(expected)
      }
    }

    it("should create a reads that fails with invalid values") {
      expectedFails.foreach {
        case (k, v) =>
          val result = reads.reads(k)
          result.isError shouldBe true
          errorMessages(result) shouldBe v
      }
    }
  }

  /**
    * Shared scenarios for testing Writes
    */
  private def testWrites(writer: Writes[Dummy], expectations: Map[Dummy, String]): Unit =
    describe("Writes") {
      it("should create a writes that writes enum values to JsString") {
        expectations.foreach {
          case (k, v) =>
            writer.writes(k) should be(JsString(v))
        }
      }
    }

  /**
    * Shared scenarios for testing Formats
    */
  private def testFormats(
      formats: Format[Dummy],
      expectedReadSuccesses: Map[String, Dummy],
      expectedReadErrors: Map[String, Seq[String]],
      expectedWrites: Map[Dummy, String]
  ): Unit = describe("Formats") {
    testReads(formats, expectedReadSuccesses, expectedReadErrors)
    testWrites(formats, expectedWrites)
  }
}
