package enumeratum
import scala.language.experimental.macros

/**
  * Created by Lloyd on 1/4/17.
  *
  * Copyright 2017
  */
object FindValEnums {
  def apply[A] = macro ValueEnumMacros.findIntValueEntriesImpl[A]
}

sealed abstract class A private (val value: Int) {
  val text: String
}

object A {

  val values = FindValEnums[A]

  case object A1 extends A(1) {
    val text = identity("something")
  }

  def identity(str: String) = str

}

// Example with outside constructor call in constructor call
class C
object C {
  def build: C = new C
}

sealed abstract class B private (val value: Int, val other: C) {
  val text: String
}
object B {

  val values = FindValEnums[B]

  case object B1 extends B(1, new C) {
    val text = identity("something")
  }
  case object B2 extends B(2, C.build) {
    val text = identity("something")
  }

  def identity(str: String) = str

}


// Test case of traits with val member fulfilling the value contract
sealed abstract trait D {
  def value: Int
}

object D {
  val values = FindValEnums[D]

  case object D1 extends D {
    val value = 1
    val text  = identity("something")
  }

  // Out of order
  case object D2 extends D {
    val text  = identity("something")
    val value = 2
  }

  def identity(str: String) = str
}
