package enumeratum.values

import org.scalatest._
import BSONValueHandlers._

/**
 * @author Alessandro Lacava (@lambdista)
 * @since 2016-04-23
 */
class EnumBsonHandlerSpec extends FunSpec with Matchers with EnumBsonHandlerHelpers {

  describe(".reader") {

    testReader("IntEnum", LibraryItem)
    testReader("LongEnum", ContentType)
    testReader("ShortEnum", Drinks)
    testReader("StringEnum", OperatingSystem)
    testReader("ByteEnum", Bites)
    testReader("CharEnum", Alphabet)

  }

  describe(".writer") {

    testWriter("IntEnum", LibraryItem)
    testWriter("LongEnum", ContentType)
    testWriter("ShortEnum", Drinks)
    testWriter("StringEnum", OperatingSystem)
    testWriter("ByteEnum", Bites)
    testWriter("CharEnum", Alphabet)

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
