package enumeratum

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.language.experimental.macros

class CompilationSpec extends AnyFunSpec with Matchers {

  describe("sanity check") {
    it("should have proper members") {
      A.values shouldBe Seq(A.A1)
      B.values shouldBe Seq(B.B1, B.B2)
      D.values shouldBe Seq(D.D1, D.D2, D.D3)
      E.values shouldBe Seq(E.E1, E.E2)
      F.values shouldBe Seq(F.F1, F.F2, F.F3, F.F4)
      G.values shouldBe Seq(G.G1)
      H.values shouldBe Seq(H.H1, H.H2, H.H3, H.H4, H.H5, H.H6)
      I.values shouldBe Seq(I.I1, I.I2, I.I3, I.I4, I.I5, I.I6)
    }
  }

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
sealed trait D {
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

  // value in value
  case object D3 extends D {

    val text = {
      val value = 2
      identity("something")
    }
    val value = 3

  }

  def identity(str: String) = str
}

// Test case for when there are scala docs

sealed trait E {
  def value: Int
}

/** The E
  */
object E {

  /** What's up?
    */
  val values = FindValEnums[E]

  /** E1
    */
  case object E1 extends E {
    val value = 1
  }

  /** E2
    */
  case object E2 extends E {
    val value = 2
  }

}

// Test case for default args
sealed abstract class F(val value: Int, val name: String = "joe")

object F {

  val values = FindValEnums[F]

  case object F1 extends F(1, "john")
  case object F2 extends F(2)
  case object F3 extends F(name = "mary", value = 3)
  case object F4 extends F(value = 4, "mike")

}

sealed abstract class G(val name: String, val value: Int)
object G {
  val values = FindValEnums[G]
  case object G1 extends G("gerald", 1)
}

sealed trait H {
  def value: Int
}

object H {
  sealed abstract class HA(override val value: Int)                   extends H
  sealed abstract class HB(val name: String, override val value: Int) extends H

  case object H1 extends HA(1)
  case object H2 extends HA(value = 2)
  case object H3 extends HB("h3", 3)
  case object H4 extends HB(name = "h4", 4)
  case object H5 extends H {
    override val value: Int = 5
  }
  case object H6 extends H {
    override val value: Int = 6
  }

  val values = FindValEnums[H]
}

sealed abstract class I(val value: Int)

object I {
  sealed abstract class IA(override val value: Int)                   extends I(value)
  sealed abstract class IB(val name: String, override val value: Int) extends I(value)

  case object I1 extends IA(1)
  case object I2 extends IA(2)
  case object I3 extends IB("i3", 3)
  case object I4 extends IB(name = "i4", 4)
  case object I5 extends I(5)
  case object I6 extends I(value = 6)

  val values = FindValEnums[I]
}
