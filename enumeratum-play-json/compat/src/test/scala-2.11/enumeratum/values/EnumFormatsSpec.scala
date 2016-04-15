package enumeratum.values

import org.scalatest._

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */
class EnumFormatsSpec extends FunSpec with Matchers with EnumJsonFormatHelpers {

  describe(".reads") {

    testReads("IntEnum", LibraryItem)
    testReads("LongEnum", ContentType)
    testReads("ShortEnum", Drinks)

  }

  describe(".writes") {

    testWrites("IntEnum", LibraryItem)
    testWrites("LongEnum", ContentType)
    testWrites("ShortEnum", Drinks)

  }

  describe(".formats") {

    testFormats("IntEnum", LibraryItem)
    testFormats("LongEnum", ContentType)
    testFormats("ShortEnum", Drinks)
    testFormats("PlayJsonValueEnum", JsonDrinks, Some(JsonDrinks.format))

  }

}