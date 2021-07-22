package enumeratum

import org.scalatest.{FunSpec, Matchers}
import cats.syntax.either._
import io.circe.Json
import io.circe.syntax._

class CirceKeySpec extends FunSpec with Matchers {
  describe("to JSON") {
    it("should work") {
      Map(ShirtSize.Small -> 5, ShirtSize.Large -> 10).asJson shouldBe Json.obj(
        "Small" -> 5.asJson,
        "Large" -> 10.asJson
      )
    }
  }

  describe("from JSON") {
    it("should work") {
      Json
        .obj(
          "Medium" -> 100.asJson,
          "Large"  -> 15.asJson
        )
        .as[Map[ShirtSize, Int]] shouldBe Map(
        ShirtSize.Medium -> 100,
        ShirtSize.Large  -> 15
      ).asRight
    }
  }
}
