package enumeratum

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import zio.json._

class ZioJsonSpec extends AnyFunSpec with Matchers {

  describe("to JSON") {

    it("should work") {
      ZioJsonShirtSize.values.foreach { entry =>
        entry.toJson shouldBe s""""${entry.entryName}""""
      }
    }

    it("should work for lower case") {
      implicit val enc: JsonEncoder[ZioJsonShirtSize] =
        ZioJson.encoderLowercase(ZioJsonShirtSize)
      ZioJsonShirtSize.values.foreach { entry =>
        entry.toJson shouldBe s""""${entry.entryName.toLowerCase}""""
      }
    }

    it("should work for upper case") {
      implicit val enc: JsonEncoder[ZioJsonShirtSize] =
        ZioJson.encoderUppercase(ZioJsonShirtSize)
      ZioJsonShirtSize.values.foreach { entry =>
        entry.toJson shouldBe s""""${entry.entryName.toUpperCase}""""
      }
    }

  }

  describe("from JSON") {

    it("should parse to members when given proper JSON") {
      ZioJsonShirtSize.values.foreach { entry =>
        s""""${entry.entryName}"""".fromJson[ZioJsonShirtSize] shouldBe Right(entry)
      }
    }

    it("should parse to members when given proper JSON for lower case") {
      implicit val dec: JsonDecoder[ZioJsonShirtSize] =
        ZioJson.decoderLowercaseOnly(ZioJsonShirtSize)
      ZioJsonShirtSize.values.foreach { entry =>
        s""""${entry.entryName.toLowerCase}"""".fromJson[ZioJsonShirtSize] shouldBe Right(entry)
      }
    }

    it("should parse to members when given proper JSON for upper case") {
      implicit val dec: JsonDecoder[ZioJsonShirtSize] =
        ZioJson.decoderUppercaseOnly(ZioJsonShirtSize)
      ZioJsonShirtSize.values.foreach { entry =>
        s""""${entry.entryName.toUpperCase}"""".fromJson[ZioJsonShirtSize] shouldBe Right(entry)
      }
    }

    it("should parse to members when given proper JSON for ignoring case") {
      implicit val dec: JsonDecoder[ZioJsonShirtSize] =
        ZioJson.decoderCaseInsensitive(ZioJsonShirtSize)
      ZioJsonShirtSize.values.zipWithIndex.foreach { case (entry, i) =>
        val entryName =
          if (i % 2 == 0)
            entry.entryName.toUpperCase
          else
            entry.entryName.toLowerCase
        s""""$entryName"""".fromJson[ZioJsonShirtSize] shouldBe Right(entry)
      }
    }

    it("should fail to parse to members when given improper JSON, even when ignoring case") {
      implicit val dec: JsonDecoder[ZioJsonShirtSize] =
        ZioJson.decoderCaseInsensitive(ZioJsonShirtSize)
      Seq("123", "Jumbo").foreach { s =>
        s""""$s"""".fromJson[ZioJsonShirtSize] shouldBe a[Left[_, _]]
      }
    }

    it("should fail to parse random JSON to members") {
      """"XXL"""".fromJson[ZioJsonShirtSize] shouldBe a[Left[_, _]]
      "123".fromJson[ZioJsonShirtSize] shouldBe a[Left[_, _]]
    }

    it("should fail to parse mixed but not upper case") {
      implicit val dec: JsonDecoder[ZioJsonShirtSize] =
        ZioJson.decoderUppercaseOnly(ZioJsonShirtSize)
      Seq("Small", "Medium", "Large").foreach { s =>
        s""""$s"""".fromJson[ZioJsonShirtSize] shouldBe a[Left[_, _]]
      }
    }

    it("should fail to parse mixed but not lower case") {
      implicit val dec: JsonDecoder[ZioJsonShirtSize] =
        ZioJson.decoderLowercaseOnly(ZioJsonShirtSize)
      Seq("Small", "Medium", "Large").foreach { s =>
        s""""$s"""".fromJson[ZioJsonShirtSize] shouldBe a[Left[_, _]]
      }
    }

  }

}
