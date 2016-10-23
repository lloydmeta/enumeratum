package enumeratum.values

import org.scalatest.{FunSpec, Matchers}
import argonaut._
import Argonaut._

/**
  * Created by alonsodomin on 14/10/2016.
  */
class ArgonautValueEnumSpec extends FunSpec with Matchers {

  testArgonautEnum("LongArgonautEnum", ArgonautMediaType)
  testArgonautEnum("IntArgonautEnum", ArgonautJsonLibs)
  testArgonautEnum("ShortArgonautEnum", ArgonautDevice)
  testArgonautEnum("CharArgonautEnum", ArgonautBool)
  testArgonautEnum("StringArgonautEnum", ArgonautHttpMethod)
  testArgonautEnum("ByteArgonautEnum", ArgonautDigits)

  private def testArgonautEnum[ValueType: EncodeJson: DecodeJson,
                               EntryType <: ValueEnumEntry[ValueType]: EncodeJson: DecodeJson](
      enumKind: String,
      enum: ValueEnum[ValueType, EntryType] with ArgonautValueEnum[ValueType, EntryType]): Unit = {
    describe(enumKind) {

      describe("from JSON") {
        it("should work") {
          enum.values.foreach { entry =>
            entry.asJson shouldBe entry.value.asJson
          }
        }
      }

      describe("from JSON") {
        it("should parse members when passing proper JSON values") {
          enum.values.foreach { entry =>
            entry.asJson.as[EntryType] shouldBe okResult(entry)
          }
        }

        it("should fail to parse random JSON value") {
          val results = Seq("NO".asJson, Long.MinValue.asJson).map(_.as[EntryType])
          results.foreach { res =>
            res.result.isLeft shouldBe true
            res.history.map(_.toList) shouldBe Some(Nil)
          }
        }
      }
    }
  }

}

sealed abstract class ArgonautMediaType(val value: Long, name: String) extends LongEnumEntry
case object ArgonautMediaType
    extends LongEnum[ArgonautMediaType]
    with LongArgonautEnum[ArgonautMediaType] {
  case object `text/json`        extends ArgonautMediaType(1L, "text/json")
  case object `text/html`        extends ArgonautMediaType(2L, "text/html")
  case object `application/jpeg` extends ArgonautMediaType(3L, "application/jpeg")

  val values = findValues
}

sealed abstract class ArgonautJsonLibs(val value: Int) extends IntEnumEntry
case object ArgonautJsonLibs
    extends IntEnum[ArgonautJsonLibs]
    with IntArgonautEnum[ArgonautJsonLibs] {
  case object Json4s    extends ArgonautJsonLibs(1)
  case object Argonaut  extends ArgonautJsonLibs(2)
  case object Circe     extends ArgonautJsonLibs(3)
  case object PlayJson  extends ArgonautJsonLibs(4)
  case object SprayJson extends ArgonautJsonLibs(5)
  case object UPickle   extends ArgonautJsonLibs(6)

  val values = findValues
}

sealed abstract class ArgonautDevice(val value: Short) extends ShortEnumEntry
case object ArgonautDevice
    extends ShortEnum[ArgonautDevice]
    with ShortArgonautEnum[ArgonautDevice] {
  case object Phone   extends ArgonautDevice(1)
  case object Laptop  extends ArgonautDevice(2)
  case object Desktop extends ArgonautDevice(3)
  case object Tablet  extends ArgonautDevice(4)

  val values = findValues
}

sealed abstract class ArgonautHttpMethod(val value: String) extends StringEnumEntry
case object ArgonautHttpMethod
    extends StringEnum[ArgonautHttpMethod]
    with StringArgonautEnum[ArgonautHttpMethod] {
  case object Get  extends ArgonautHttpMethod("GET")
  case object Put  extends ArgonautHttpMethod("PUT")
  case object Post extends ArgonautHttpMethod("POST")

  val values = findValues
}

sealed abstract class ArgonautBool(val value: Char) extends CharEnumEntry
case object ArgonautBool extends CharEnum[ArgonautBool] with CharArgonautEnum[ArgonautBool] {
  case object True  extends ArgonautBool('T')
  case object False extends ArgonautBool('F')
  case object Maybe extends ArgonautBool('?')

  val values = findValues
}

sealed abstract class ArgonautDigits(val value: Byte) extends ByteEnumEntry
case object ArgonautDigits extends ByteEnum[ArgonautDigits] with ByteArgonautEnum[ArgonautDigits] {
  case object Uno  extends ArgonautDigits(1)
  case object Dos  extends ArgonautDigits(2)
  case object Tres extends ArgonautDigits(3)

  val values = findValues
}
