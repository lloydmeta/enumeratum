package enumeratum.values

/**
  * @author Alessandro Lacava 
  * @since 2016-04-23
  */
sealed abstract class BsonDrinks(val value: Short, name: String) extends ShortEnumEntry

case object BsonDrinks extends ShortEnum[BsonDrinks] with ShortReactiveMongoBsonValueEnum[BsonDrinks] {

  case object OrangeJuice extends BsonDrinks(value = 1, name = "oj")
  case object AppleJuice extends BsonDrinks(value = 2, name = "aj")
  case object Cola extends BsonDrinks(value = 3, name = "cola")
  case object Beer extends BsonDrinks(value = 4, name = "beer")

  val values = findValues

}
