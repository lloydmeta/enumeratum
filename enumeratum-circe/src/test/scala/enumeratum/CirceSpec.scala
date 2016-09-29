package enumeratum

import org.scalatest.{ FunSpec, Matchers }
import cats.data.Xor
import io.circe.Json
import io.circe.syntax._

/**
 * Created by Lloyd on 4/14/16.
 *
 * Copyright 2016
 */
class CirceSpec extends FunSpec with Matchers {

  describe("to JSON") {

    it("should work") {
      ShirtSize.values.foreach { entry =>
        entry.asJson shouldBe Json.fromString(entry.entryName)
      }
    }

    it("should work for lower case") {
      ShirtSize.values.foreach { entry =>
        entry.asJson(Circe.encoderLowercase(ShirtSize)) shouldBe Json.fromString(entry.entryName.toLowerCase)
      }
    }

    it("should work for upper case") {
      ShirtSize.values.foreach { entry =>
        entry.asJson(Circe.encoderUppercase(ShirtSize)) shouldBe Json.fromString(entry.entryName.toUpperCase)
      }
    }

  }

  describe("from Json") {

    it("should parse to members when given proper JSON") {
      ShirtSize.values.foreach { entry =>
        Json.fromString(entry.entryName).as[ShirtSize] shouldBe Xor.Right(entry)
      }
    }

    it("should parse to members when given proper JSON for lower case") {
      ShirtSize.values.foreach { entry =>
        Json.fromString(entry.entryName.toLowerCase).as[ShirtSize](Circe.decoderLowercaseOnly(ShirtSize)) shouldBe Xor.Right(entry)
      }
    }

    it("should parse to members when given proper JSON for upper case") {
      ShirtSize.values.foreach { entry =>
        Json.fromString(entry.entryName.toUpperCase).as[ShirtSize](Circe.decoderUppercaseOnly(ShirtSize)) shouldBe Xor.Right(entry)
      }
    }

    it("should fail to parse random JSON to members") {
      val failures = Seq(Json.fromString("XXL"), Json.fromInt(Int.MaxValue)).map(j => j.as[ShirtSize](Circe.decoder(ShirtSize)))
      failures.foreach { f =>
        f.isLeft shouldBe true
        f.leftMap(_.history shouldBe Nil)
      }
    }

    it("should fail to parse mixed but not upper case") {
      val failures = Seq("Small", "Medium", "Large").map(s => Json.fromString(s).as[ShirtSize](Circe.decoderUppercaseOnly(ShirtSize)))
      failures.foreach { f =>
        f.isLeft shouldBe true
        f.leftMap(_.history shouldBe Nil)
      }
    }

    it("should fail to parse mixed but not lower case") {
      val failures = Seq("Small", "Medium", "Large").map(s => Json.fromString(s).as[ShirtSize](Circe.decoderLowercaseOnly(ShirtSize)))
      failures.foreach { f =>
        f.isLeft shouldBe true
        f.leftMap(_.history shouldBe Nil)
      }
    }

  }

}
