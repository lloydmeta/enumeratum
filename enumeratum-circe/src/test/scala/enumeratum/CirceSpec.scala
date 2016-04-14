package enumeratum

import cats.data.Xor
import io.circe.Json
import org.scalatest.{ FunSpec, Matchers }
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

  }

  describe("from Json") {

    it("should parse to members when given proper JSON") {
      ShirtSize.values.foreach { entry =>
        Json.fromString(entry.entryName).as[ShirtSize] shouldBe Xor.Right(entry)
      }
    }

    it("should fail to parse random JSON to members") {
      Json.fromString("XXL").as[ShirtSize].isLeft shouldBe true
      Json.fromInt(Int.MaxValue).as[ShirtSize].isLeft shouldBe true
    }

  }

}
