package enumeratum.values

import org.scalatest._
import play.api.libs.json.JsString

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

  }

  describe(".writes") {

    testNumericWrites("IntEnum", LibraryItem)
    testNumericWrites("LongEnum", ContentType)
    testNumericWrites("ShortEnum", Drinks)
    testWrites("StringEnum", OperatingSystem, JsString)

  }

  describe(".formats") {

    testNumericFormats("IntEnum", LibraryItem)
    testNumericFormats("LongEnum", ContentType)
    testNumericFormats("ShortEnum", Drinks)
    testFormats("StringEnum", OperatingSystem, JsString)
    testNumericFormats("PlayJsonValueEnum", JsonDrinks, Some(JsonDrinks.format))

  }

}