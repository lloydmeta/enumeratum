package enumeratum

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class ScalacheckSpec
    extends AnyFunSpec
    with ScalaCheckDrivenPropertyChecks
    with Matchers
    with ScalacheckTest {

  import scalacheck._

  test[EnumEntry, InTheWoods.Mushroom]("EnumEntry")

}
