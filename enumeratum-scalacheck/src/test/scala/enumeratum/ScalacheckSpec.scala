package enumeratum

import org.scalatest.{FunSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class ScalacheckSpec
    extends FunSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with ScalacheckTest {

  import scalacheck._

  test[EnumEntry, InTheWoods.Mushroom]

}
