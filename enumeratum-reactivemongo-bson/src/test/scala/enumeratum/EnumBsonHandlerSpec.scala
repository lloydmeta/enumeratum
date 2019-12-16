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

  testScenario(
    descriptor = "case insensitive",
    reader = EnumHandler.reader(enum = Dummy, insensitive = true),
    expectedReadSuccesses = Map("A" -> Dummy.A, "a" -> Dummy.A, "C" -> Dummy.c),
    expectedReadFails = Nil,
    writer = EnumHandler.writer(Dummy),
    expectedWrites = Map(Dummy.A -> "A", Dummy.c -> "c"),
    handler = EnumHandler.handler(Dummy, insensitive = true)
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

  testScenario(
    descriptor = "upper case transformed",
    reader = EnumHandler.readerUppercaseOnly(Dummy),
    expectedReadSuccesses = Map("A" -> Dummy.A, "B" -> Dummy.B, "C" -> Dummy.c),
    expectedReadFails = Seq("c"),
    writer = EnumHandler.writerUppercase(Dummy),
    expectedWrites = Map(Dummy.A -> "A", Dummy.c -> "C"),
    handler = EnumHandler.handlerUppercaseOnly(Dummy)
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

    describe("BJSONHandler") {
      describe("reading") {
        readTests(handler)
      }
      describe("writing") {
        writeTests(handler)
      }
    }
  }

}
