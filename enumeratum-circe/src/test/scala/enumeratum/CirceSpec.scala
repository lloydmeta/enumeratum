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
      Json.fromString("XXL").as[ShirtSize].isLeft shouldBe true
      Json.fromInt(Int.MaxValue).as[ShirtSize].isLeft shouldBe true
    }

    it("should fail to parse mixed but not upper case") {
      Json.fromString("Small").as[ShirtSize](Circe.decoderUppercaseOnly(ShirtSize)).isLeft shouldBe true
      Json.fromString("Medium").as[ShirtSize](Circe.decoderUppercaseOnly(ShirtSize)).isLeft shouldBe true
      Json.fromString("Large").as[ShirtSize](Circe.decoderUppercaseOnly(ShirtSize)).isLeft shouldBe true
    }

    it("should fail to parse mixed but not lower case") {
      Json.fromString("Small").as[ShirtSize](Circe.decoderLowercaseOnly(ShirtSize)).isLeft shouldBe true
      Json.fromString("Medium").as[ShirtSize](Circe.decoderLowercaseOnly(ShirtSize)).isLeft shouldBe true
      Json.fromString("Large").as[ShirtSize](Circe.decoderLowercaseOnly(ShirtSize)).isLeft shouldBe true
    }

  }

}
