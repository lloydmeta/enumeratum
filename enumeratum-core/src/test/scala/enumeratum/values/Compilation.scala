package enumeratum.values

/** Created by Lloyd on 1/4/17.
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
    val text = identity("something")
  }

  def identity(str: String) = str

}

class C
object C {
  def build: C = new C
}

sealed abstract class B private (val value: Int, val other: C) extends IntEnumEntry {
  val text: String
}

object B extends IntEnum[B] {

  val values = findValues

  case object B1 extends B(1, new C) {
    val text = identity("something")
  }
  case object B2 extends B(2, C.build) {
    val text = identity("something")
  }

  def identity(str: String) = str

}
