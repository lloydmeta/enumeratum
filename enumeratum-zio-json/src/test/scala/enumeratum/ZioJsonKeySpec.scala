package enumeratum

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import zio.json._

class ZioJsonKeySpec extends AnyFunSpec with Matchers {

  describe("to JSON") {
    it("should work") {
      Map(ZioJsonShirtSize.Small -> 5, ZioJsonShirtSize.Large -> 10).toJson
        .fromJson[Map[String, Int]] shouldBe Right(Map("Small" -> 5, "Large" -> 10))
    }
  }

  describe("from JSON") {
    it("should work") {
      """{"Medium":100,"Large":15}"""
        .fromJson[Map[ZioJsonShirtSize, Int]] shouldBe Right(
        Map(
          ZioJsonShirtSize.Medium -> 100,
          ZioJsonShirtSize.Large  -> 15
        )
      )
    }

    it("should fail for invalid keys") {
      """{"XXL":100}"""
        .fromJson[Map[ZioJsonShirtSize, Int]] shouldBe a[Left[_, _]]
    }
  }

}
