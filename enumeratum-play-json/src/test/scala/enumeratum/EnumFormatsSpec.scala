package enumeratum

import org.scalatest.OptionValues._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._

class EnumFormatsSpec extends AnyFunSpec with Matchers {

  testScenario(
    descriptor = "normal operation",
    reads = EnumFormats.reads(Dummy),
    readSuccessExpectations = Map("A" -> Dummy.A),
    readErrors = Map("C" -> ReadError.onlyMessages(Seq("error.expected.validenumvalue"))),
    writes = EnumFormats.writes(Dummy),
    writeExpectations = Map(Dummy.A -> "A"),
    formats = EnumFormats.formats(Dummy)
  )

  testKeyScenario(
    descriptor = "normal operation",
    reads = EnumFormats.keyReads(Dummy),
    readSuccessExpectations = Map("A" -> Dummy.A),
    readErrors = Map("C" -> Seq("error.expected.validenumvalue")),
    writes = EnumFormats.keyWrites(Dummy),
    writeExpectations = Map(Dummy.A -> "A")
  )

  testScenario(
    descriptor = "case insensitive",
    reads = EnumFormats.reads(e = Dummy, insensitive = true),
    readSuccessExpectations = Map(
      "A" -> Dummy.A,
      "a" -> Dummy.A
    ),
    readErrors = Map.empty,
    writes = EnumFormats.writes(Dummy),
    writeExpectations = Map(Dummy.A -> "A"),
    formats = EnumFormats.formats(e = Dummy, insensitive = true)
  )

  testKeyScenario(
    descriptor = "case insensitive",
    reads = EnumFormats.keyReads(e = Dummy, insensitive = true),
    readSuccessExpectations = Map(
      "A" -> Dummy.A,
      "a" -> Dummy.A
    ),
    readErrors = Map.empty,
    writes = EnumFormats.keyWrites(Dummy),
    writeExpectations = Map(Dummy.A -> "A")
  )

  testScenario(
    descriptor = "lower case transformed",
    reads = EnumFormats.readsLowercaseOnly(Dummy),
    readSuccessExpectations = Map(
      "a" -> Dummy.A
    ),
    readErrors = Map(
      "A" -> ReadError.onlyMessages(Seq("error.expected.validenumvalue"))
    ),
    writes = EnumFormats.writesLowercaseOnly(Dummy),
    writeExpectations = Map(Dummy.A -> "a"),
    formats = EnumFormats.formatsLowerCaseOnly(Dummy)
  )

  testKeyScenario(
    descriptor = "lower case transformed",
    reads = EnumFormats.keyReadsLowercaseOnly(Dummy),
    readSuccessExpectations = Map(
      "a" -> Dummy.A
    ),
    readErrors = Map(
      "A" -> Seq("error.expected.validenumvalue")
    ),
    writes = EnumFormats.keyWritesLowercaseOnly(Dummy),
    writeExpectations = Map(Dummy.A -> "a")
  )

  testScenario(
    descriptor = "upper case transformed",
    reads = EnumFormats.readsUppercaseOnly(Dummy),
    readSuccessExpectations = Map(
      "A" -> Dummy.A,
      "C" -> Dummy.c
    ),
    readErrors = Map(
      "a" -> ReadError.onlyMessages(Seq("error.expected.validenumvalue"))
    ),
    writes = EnumFormats.writesUppercaseOnly(Dummy),
    writeExpectations = Map(Dummy.A -> "A"),
    formats = EnumFormats.formatsUppercaseOnly(Dummy)
  )

  testKeyScenario(
    descriptor = "upper case transformed",
    reads = EnumFormats.keyReadsUppercaseOnly(Dummy),
    readSuccessExpectations = Map(
      "A" -> Dummy.A,
      "C" -> Dummy.c
    ),
    readErrors = Map(
      "a" -> Seq("error.expected.validenumvalue")
    ),
    writes = EnumFormats.keyWritesUppercaseOnly(Dummy),
    writeExpectations = Map(Dummy.A -> "A")
  )

  // Bunch of shared testing methods

  private def errorMessages(jsResult: JsResult[_]): scala.collection.Seq[String] =
    jsResult.fold(
      _.collect { case (path, errors) =>
        errors.map(_.message).mkString
      },
      _ => Seq.empty
    )

  private def errorArgs(jsResult: JsResult[_]): scala.collection.Seq[String] =
    jsResult.fold(
      _.collect { case (path, errors) =>
        errors.map(_.args.mkString(", ")).mkString
      },
      _ => Seq.empty
    )

  private def testScenario(
      descriptor: String,
      reads: Reads[Dummy],
      readSuccessExpectations: Map[String, Dummy],
      readErrors: Map[String, ReadError],
      writes: Writes[Dummy],
      writeExpectations: Map[Dummy, String],
      formats: Format[Dummy]
  ): Unit = describe(descriptor) {
    testReads(reads, readSuccessExpectations, readErrors)
    testWrites(writes, writeExpectations)
    testFormats(formats, readSuccessExpectations, readErrors, writeExpectations)
  }

  /** Shared scenarios for testing Reads
    */
  private def testReads(
      reads: Reads[Dummy],
      expectedSuccesses: Map[String, Dummy],
      expectedErrors: Map[String, ReadError]
  ): Unit = describe("Reads") {
    val expectedFails: Map[JsValue, ReadError] = {
      val withJsValueKeys = expectedErrors.map { case (k, v) => JsString(k) -> v }
      // Add standard errors
      (withJsValueKeys ++ Map(
        JsNumber(2)   -> ReadError.onlyMessages(Seq("error.expected.enumstring")),
        JsString("D") -> ReadError.onlyMessages(Seq("error.expected.validenumvalue"))
      )).toMap
    }

    it("should create a reads that works with valid values") {
      expectedSuccesses.foreach { case (name, expected) =>
        reads.reads(JsString(name)).asOpt.value should be(expected)
      }
    }

    it("should create a reads that fails with invalid values") {
      expectedFails.foreach { case (k, v) =>
        val result = reads.reads(k)
        result.isError shouldBe true
        errorMessages(result) shouldBe v.errorMessages
        errorArgs(result) shouldBe v.errorArgs
      }
    }
  }

  /** Shared scenarios for testing Writes
    */
  private def testWrites(writer: Writes[Dummy], expectations: Map[Dummy, String]): Unit =
    describe("Writes") {
      it("should create a writes that writes enum values to JsString") {
        expectations.foreach { case (k, v) =>
          writer.writes(k) should be(JsString(v))
        }
      }
    }

  /** Shared scenarios for testing Formats
    */
  private def testFormats(
      formats: Format[Dummy],
      expectedReadSuccesses: Map[String, Dummy],
      expectedReadErrors: Map[String, ReadError],
      expectedWrites: Map[Dummy, String]
  ): Unit = describe("Formats") {
    testReads(formats, expectedReadSuccesses, expectedReadErrors)
    testWrites(formats, expectedWrites)
  }

  private def testKeyScenario(
      descriptor: String,
      reads: KeyReads[Dummy],
      readSuccessExpectations: Map[String, Dummy],
      readErrors: Map[String, Seq[String]],
      writes: KeyWrites[Dummy],
      writeExpectations: Map[Dummy, String]
  ): Unit = describe(descriptor) {
    testKeyReads(reads, readSuccessExpectations, readErrors)
    testKeyWrites(writes, writeExpectations)
  }

  private def testKeyReads(
      reads: KeyReads[Dummy],
      expectedSuccesses: Map[String, Dummy],
      expectedErrors: Map[String, Seq[String]]
  ): Unit = describe("KeyReads") {
    it("should create a KeyReads that works with valid values") {
      expectedSuccesses.foreach { case (name, expected) =>
        reads.readKey(name).asOpt.value should be(expected)
      }
    }

    it("should create a KeyReads that fails with invalid values") {
      expectedErrors.foreach { case (k, v) =>
        val result = reads.readKey(k)
        result.isError shouldBe true
        errorMessages(result) shouldBe v
      }
    }
  }

  /** Shared scenarios for testing KeyWrites
    */
  private def testKeyWrites(writer: KeyWrites[Dummy], expectations: Map[Dummy, String]): Unit =
    describe("KeyWrites") {
      it("should create a KeyWrites that writes enum values to String") {
        expectations.foreach { case (k, v) =>
          writer.writeKey(k) should be(v)
        }
      }
    }
}
