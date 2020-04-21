package enumeratum

import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class ScalacheckSpec
    extends FunSpec
    with ScalaCheckDrivenPropertyChecks
    with Matchers
    with ScalacheckTest {

  import scalacheck._

  test[EnumEntry, InTheWoods.Mushroom]("EnumEntry")

}
