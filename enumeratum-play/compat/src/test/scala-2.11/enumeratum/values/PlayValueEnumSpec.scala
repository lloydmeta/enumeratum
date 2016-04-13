package enumeratum.values

import org.scalatest.{ FunSpec, Matchers }

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */
class PlayValueEnumSpec extends FunSpec with Matchers with PlayValueEnumHelpers {

  testPlayEnum("PlayLongEnum", PlayContentType)
  testPlayEnum("PlayShortEnum", PlayDrinks)
  testPlayEnum("PlayIntEnum", PlayLibraryItem)
  testPlayEnum("PlayIntEnum with values declared as members", PlayMovieGenre)

}

sealed abstract class PlayContentType(val value: Long, name: String) extends LongEnumEntry

case object PlayContentType
    extends PlayLongEnum[PlayContentType] {

  val values = findValues

  case object Text extends PlayContentType(value = 1L, name = "text")
  case object Image extends PlayContentType(value = 2L, name = "image")
  case object Video extends PlayContentType(value = 3L, name = "video")
  case object Audio extends PlayContentType(value = 4L, name = "audio")

}

sealed abstract class PlayDrinks(val value: Short, name: String) extends ShortEnumEntry

case object PlayDrinks extends PlayShortEnum[PlayDrinks] {

  case object OrangeJuice extends PlayDrinks(value = 1, name = "oj")
  case object AppleJuice extends PlayDrinks(value = 2, name = "aj")
  case object Cola extends PlayDrinks(value = 3, name = "cola")
  case object Beer extends PlayDrinks(value = 4, name = "beer")

  val values = findValues

}

sealed abstract class PlayLibraryItem(val value: Int, val name: String) extends IntEnumEntry

case object PlayLibraryItem extends PlayIntEnum[PlayLibraryItem] {

  // A good mix of named, unnamed, named + unordered args
  case object Book extends PlayLibraryItem(value = 1, name = "book")
  case object Movie extends PlayLibraryItem(name = "movie", value = 2)
  case object Magazine extends PlayLibraryItem(3, "magazine")
  case object CD extends PlayLibraryItem(4, name = "cd")

  val values = findValues

}

sealed abstract class PlayMovieGenre extends IntEnumEntry

case object PlayMovieGenre extends PlayIntEnum[PlayMovieGenre] {

  case object Action extends PlayMovieGenre {
    val value = 1
  }
  case object Comedy extends PlayMovieGenre {
    val value: Int = 2
  }
  case object Romance extends PlayMovieGenre {
    val value = 3
  }

  val values = findValues

}
