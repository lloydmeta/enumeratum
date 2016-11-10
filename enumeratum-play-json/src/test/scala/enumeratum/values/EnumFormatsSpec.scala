package enumeratum.values

import org.scalatest._
import EnumFormats._
import play.api.libs.json.{ JsNumber, JsString }

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */
class EnumFormatsSpec extends FunSpec with Matchers with EnumJsonFormatHelpers {

  describe(".reads") {

    testNumericReads("IntEnum", LibraryItem)
    testNumericReads("LongEnum", ContentType)
    testNumericReads("ShortEnum", Drinks)
    testReads("StringEnum", OperatingSystem, JsString)
    testReads("CharEnum", Alphabet, { c: Char =>
      JsString(s"$c")
    })
    testReads("ByteEnum", Bites, { b: Byte =>
      JsNumber(b)
    })

  }

  describe(".writes") {

    testNumericWrites("IntEnum", LibraryItem)
    testNumericWrites("LongEnum", ContentType)
    testNumericWrites("ShortEnum", Drinks)
    testWrites("StringEnum", OperatingSystem, JsString)
    testWrites("CharEnum", Alphabet, { c: Char =>
      JsString(s"$c")
    })
    testWrites("ByteEnum", Bites, { b: Byte =>
      JsNumber(b)
    })

  }

  describe(".formats") {

    testNumericFormats("IntEnum", LibraryItem)
    testNumericFormats("LongEnum", ContentType)
    testNumericFormats("ShortEnum", Drinks)
    testFormats("StringEnum", OperatingSystem, JsString)
    testFormats("ByteEnum", Bites, { b: Byte =>
      JsNumber(b.toInt)
    })
    testNumericFormats("PlayJsonValueEnum", JsonDrinks, Some(JsonDrinks.format))

  }

}
