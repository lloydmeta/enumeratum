package enumeratum.values

/**
 * Created by Lloyd on 4/11/16.
 *
 * Copyright 2016
 */

sealed abstract class LibraryItem(val value: Int, val name: String) extends IntEnumEntry

case object LibraryItem extends IntEnum[LibraryItem] {

  /*
   - A good mix of named, unnamed, named + unordered args
   - Values are not in ordered consecutive order
    */
  case object Movie extends LibraryItem(name = "movie", value = 2)
  case object Book extends LibraryItem(value = 1, name = "book")
  case object Magazine extends LibraryItem(10, "magazine")
  case object CD extends LibraryItem(14, name = "cd")

  val values = findValues

}

case object Newspaper extends LibraryItem(5, "Zeitung")