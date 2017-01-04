package enumeratum.values

/**
  * Created by Lloyd on 1/4/17.
  *
  * Copyright 2017
  */
// From https://github.com/lloydmeta/enumeratum/issues/96

sealed abstract class A private (val value: Int) extends IntEnumEntry {
  val text: String
}

object A extends IntEnum[A] {

  val values = findValues

  case object A1 extends A(1) {
    val text = identity("something") // Error:(9, 16) object A1 has a value with the wrong type: something:class java.lang.String, instead of int.
    //val text = "something"
  }

  def identity(str: String) = str

}
