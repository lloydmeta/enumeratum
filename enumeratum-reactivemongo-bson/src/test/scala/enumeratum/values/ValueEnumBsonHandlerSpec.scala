package enumeratum.values

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** @author
  *   Alessandro Lacava (@lambdista)
  * @since 2016-04-23
  */
final class ValueEnumBsonHandlerSpec
    extends AnyFunSpec
    with Matchers
    with ValueEnumBsonHandlerHelpers {

  describe(".reader") {
    testReader("IntEnum", LibraryItem)
    testReader("LongEnum", ContentType)
    testReader("ShortEnum", Drinks)
    testReader("StringEnum", OperatingSystem)
    testReader("ByteEnum", Bites)
    testReader("CharEnum", Alphabet)
  }

  describe(".keyReader") {
    testKeyReader("IntEnum", LibraryItem)
    testKeyReader("LongEnum", ContentType)
    testKeyReader("ShortEnum", Drinks)
    testKeyReader("StringEnum", OperatingSystem)
    testKeyReader("ByteEnum", Bites)
    testKeyReader("CharEnum", Alphabet)
  }

  describe(".writer") {
    testWriter("IntEnum", LibraryItem)
    testWriter("LongEnum", ContentType)
    testWriter("ShortEnum", Drinks)
    testWriter("StringEnum", OperatingSystem)
    testWriter("ByteEnum", Bites)
    testWriter("CharEnum", Alphabet)
  }

  describe(".keyWriter") {
    testKeyWriter("IntEnum", LibraryItem)
    testKeyWriter("LongEnum", ContentType)
    testKeyWriter("ShortEnum", Drinks)
    testKeyWriter("StringEnum", OperatingSystem)
    testKeyWriter("ByteEnum", Bites)
    testKeyWriter("CharEnum", Alphabet)
  }

  describe(".handler") {

    testHandler("IntEnum", LibraryItem)
    testHandler("LongEnum", ContentType)
    testHandler("ShortEnum", Drinks)
    testHandler("StringEnum", OperatingSystem)
    testHandler("ShortReactiveMongoBsonValueEnum", BsonDrinks, Some(BsonDrinks.bsonHandler))
    testHandler(
      "LongReactiveMongoBsonValueEnum",
      BsonContentType,
      Some(BsonContentType.bsonHandler)
    )
    testHandler(
      "IntReactiveMongoBsonValueEnum",
      BsonLibraryItem,
      Some(BsonLibraryItem.bsonHandler)
    )
    testHandler(
      "StringReactiveMongoBsonValueEnum",
      BsonOperatingSystem,
      Some(BsonOperatingSystem.bsonHandler)
    )
    testHandler("CharReactiveMongoBsonValueEnum", BsonAlphabet, Some(BsonAlphabet.bsonHandler))
    testHandler("ByteReactiveMongoBsonValueEnum", BsonBites, Some(BsonBites.bsonHandler))

  }

}
