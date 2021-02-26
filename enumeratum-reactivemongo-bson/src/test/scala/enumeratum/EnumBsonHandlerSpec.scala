package enumeratum

import org.scalatest.OptionValues._
import org.scalatest.{FunSpec, Matchers}
import reactivemongo.api.bson._

import scala.util.Success

/**
  *
  * @author Alessandro Lacava (@lambdista)
  * @since 2016-04-23
  */
class EnumBsonHandlerSpec extends FunSpec with Matchers {

  testScenario(
    descriptor = "normal operation (no transformations)",
    reader = EnumHandler.reader(Dummy),
    expectedReadSuccesses = Map("A" -> Dummy.A, "c" -> Dummy.c),
    expectedReadFails = Seq("C"),
    writer = EnumHandler.writer(Dummy),
    expectedWrites = Map(Dummy.A -> "A", Dummy.c -> "c"),
    handler = EnumHandler.handler(Dummy)
  )

  testKeyScenario(
    descriptor = "normal operation (no transformations)",
    reader = EnumHandler.keyReader(Dummy),
    expectedReadSuccesses = Map("A" -> Dummy.A, "c" -> Dummy.c),
    expectedReadFails = Seq("C"),
    writer = EnumHandler.keyWriter(Dummy),
    expectedWrites = Map(Dummy.A -> "A", Dummy.c -> "c")
  )

  testScenario(
    descriptor = "case insensitive",
    reader = EnumHandler.reader(enum = Dummy, insensitive = true),
    expectedReadSuccesses = Map("A" -> Dummy.A, "a" -> Dummy.A, "C" -> Dummy.c),
    expectedReadFails = Nil,
    writer = EnumHandler.writer(Dummy),
    expectedWrites = Map(Dummy.A -> "A", Dummy.c -> "c"),
    handler = EnumHandler.handler(Dummy, insensitive = true)
  )

  testKeyScenario(
    descriptor = "case insensitive",
    reader = EnumHandler.keyReader(enum = Dummy, insensitive = true),
    expectedReadSuccesses = Map("A" -> Dummy.A, "a" -> Dummy.A, "C" -> Dummy.c),
    expectedReadFails = Nil,
    writer = EnumHandler.keyWriter(Dummy),
    expectedWrites = Map(Dummy.A -> "A", Dummy.c -> "c")
  )

  testScenario(
    descriptor = "lower case transformed",
    reader = EnumHandler.readerLowercaseOnly(Dummy),
    expectedReadSuccesses = Map("a" -> Dummy.A, "b" -> Dummy.B, "c" -> Dummy.c),
    expectedReadFails = Seq("A", "B", "C"),
    writer = EnumHandler.writerLowercase(Dummy),
    expectedWrites = Map(Dummy.A -> "a", Dummy.c -> "c"),
    handler = EnumHandler.handlerLowercaseOnly(Dummy)
  )

  testKeyScenario(
    descriptor = "lower case transformed",
    reader = EnumHandler.keyReaderLowercaseOnly(Dummy),
    expectedReadSuccesses = Map("a" -> Dummy.A, "b" -> Dummy.B, "c" -> Dummy.c),
    expectedReadFails = Seq("A", "B", "C"),
    writer = EnumHandler.keyWriterLowercase(Dummy),
    expectedWrites = Map(Dummy.A -> "a", Dummy.c -> "c")
  )

  testScenario(
    descriptor = "upper case transformed",
    reader = EnumHandler.readerUppercaseOnly(Dummy),
    expectedReadSuccesses = Map("A" -> Dummy.A, "B" -> Dummy.B, "C" -> Dummy.c),
    expectedReadFails = Seq("c"),
    writer = EnumHandler.writerUppercase(Dummy),
    expectedWrites = Map(Dummy.A -> "A", Dummy.c -> "C"),
    handler = EnumHandler.handlerUppercaseOnly(Dummy)
  )

  testKeyScenario(
    descriptor = "upper case transformed",
    reader = EnumHandler.keyReaderUppercaseOnly(Dummy),
    expectedReadSuccesses = Map("A" -> Dummy.A, "B" -> Dummy.B, "C" -> Dummy.c),
    expectedReadFails = Seq("c"),
    writer = EnumHandler.keyWriterUppercase(Dummy),
    expectedWrites = Map(Dummy.A -> "A", Dummy.c -> "C")
  )

  private def testScenario(
      descriptor: String,
      reader: BSONReader[Dummy],
      expectedReadSuccesses: Map[String, Dummy],
      expectedReadFails: Seq[String],
      writer: BSONWriter[Dummy],
      expectedWrites: Map[Dummy, String],
      handler: BSONHandler[Dummy]
  ): Unit = describe(descriptor) {

    val expectedReadErrors = {
      expectedReadFails.map(BSONString(_)) ++ Seq(BSONString("D"), BSONInteger(2))
    }

    def readTests(theReader: BSONReader[Dummy]): Unit = {
      it("should work with valid values") {
        expectedReadSuccesses.foreach {
          case (k, v) =>
            theReader.readOpt(BSONString(k)).value shouldBe v
        }
      }

      it("should fail with invalid values") {
        expectedReadErrors.foreach { v =>
          theReader.readOpt(v).isEmpty shouldBe true
        }
      }
    }

    def writeTests(theWriter: BSONWriter[Dummy]): Unit = {
      it("should write enum values to BSONString") {
        expectedWrites.foreach {
          case (k, v) =>
            theWriter.writeTry(k) shouldBe Success(BSONString(v))
        }
      }
    }

    describe("BSONReader") {
      readTests(reader)
    }

    describe("BSONWriter") {
      writeTests(writer)
    }

    describe("BSONHandler") {
      describe("reading") {
        readTests(handler)
      }
      describe("writing") {
        writeTests(handler)
      }
    }
  }

  private def testKeyScenario(
      descriptor: String,
      reader: KeyReader[Dummy],
      expectedReadSuccesses: Map[String, Dummy],
      expectedReadFails: Seq[String],
      writer: KeyWriter[Dummy],
      expectedWrites: Map[Dummy, String]
  ): Unit = describe(descriptor) {
    def readTests(theReader: KeyReader[Dummy]): Unit = {
      it("should work with valid keys") {
        expectedReadSuccesses.foreach {
          case (k, v) =>
            theReader.readTry(k) shouldBe Success(v)
        }
      }

      it("should fail with invalid values") {
        expectedReadFails.foreach { v =>
          theReader.readTry(v).isFailure shouldBe true
        }
      }
    }

    def writeTests(theWriter: KeyWriter[Dummy]): Unit = {
      it("should write enum values to String") {
        expectedWrites.foreach {
          case (k, v) => theWriter.writeTry(k) shouldBe Success(v)
        }
      }
    }

    describe("KeyReader") {
      readTests(reader)
    }

    describe("KeyWriter") {
      writeTests(writer)
    }
  }
}
