package enumeratum.values

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */
sealed abstract class MovieGenre extends IntEnumEntry

case object MovieGenre extends IntEnum[MovieGenre] {

  case object Action extends MovieGenre {
    val value = 1
  }
  case object Comedy extends MovieGenre {
    val value: Int = 2
  }
  case object Romance extends MovieGenre {
    val value = 3
  }

  val values = findValues

}
