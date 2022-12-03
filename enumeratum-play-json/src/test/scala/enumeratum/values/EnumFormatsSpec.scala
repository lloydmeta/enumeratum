package enumeratum.values

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import EnumFormats._
import play.api.libs.json.{JsNumber, JsString}

/** Created by Lloyd on 4/13/16.
  *
  * Copyright 2016
  */
class EnumFormatsSpec extends AnyFunSpec with Matchers with EnumJsonFormatHelpers {

  describe(".reads") {

    testNumericReads("IntEnum", LibraryItem)
    testNumericReads("LongEnum", ContentType)
    testNumericReads("ShortEnum", Drinks)
    testReads("StringEnum", OperatingSystem, JsString(_))
    testReads(
      "CharEnum",
      Alphabet,
      { (c: Char) =>
        JsString(s"$c")
      }
    )
    testReads(
      "ByteEnum",
      Bites,
      { (b: Byte) =>
        JsNumber(b.toInt)
      }
    )

  }

  describe(".writes") {

    testNumericWrites("IntEnum", LibraryItem)
    testNumericWrites("LongEnum", ContentType)
    testNumericWrites("ShortEnum", Drinks)
    testWrites("StringEnum", OperatingSystem, JsString(_))
    testWrites(
      "CharEnum",
      Alphabet,
      { (c: Char) =>
        JsString(s"$c")
      }
    )
    testWrites(
      "ByteEnum",
      Bites,
      { (b: Byte) =>
        JsNumber(b.toInt)
      }
    )

  }

  describe(".formats") {

    testNumericFormats("IntEnum", LibraryItem)
    testNumericFormats("LongEnum", ContentType)
    testNumericFormats("ShortEnum", Drinks)
    testFormats("StringEnum", OperatingSystem, JsString(_))
    testFormats(
      "ByteEnum",
      Bites,
      { (b: Byte) =>
        JsNumber(b.toInt)
      }
    )
    testNumericFormats("PlayJsonValueEnum", JsonDrinks, Some(JsonDrinks.format))

  }

}
