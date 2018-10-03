package enumeratum.values

import cats.Order
import cats.instances.int._
import cats.syntax.order._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, Matchers}

class CatsOrderValueEnumSpec extends FreeSpec with ScalaFutures with Matchers {

  "CatsOrderedValueEnum" - {
    "has a proper Order instance" - {
      val _ = implicitly[Order[OrderedTrafficLight]]
      "respect the underlying Order for equal values" in {
        ((OrderedTrafficLight.Red: OrderedTrafficLight) compare OrderedTrafficLight.Red) shouldBe 0
        ((OrderedTrafficLight.Yellow: OrderedTrafficLight) compare OrderedTrafficLight.Yellow) shouldBe 0
        ((OrderedTrafficLight.Green: OrderedTrafficLight) compare OrderedTrafficLight.Green) shouldBe 0
      }
      "respect the underlying Order for non-equal values" in {
        ((OrderedTrafficLight.Red: OrderedTrafficLight) compare OrderedTrafficLight.Yellow) < 0 shouldBe true
        ((OrderedTrafficLight.Yellow: OrderedTrafficLight) compare OrderedTrafficLight.Green) < 0 shouldBe true
        ((OrderedTrafficLight.Green: OrderedTrafficLight) compare OrderedTrafficLight.Red) > 0 shouldBe true
      }
    }
  }
}

sealed abstract class OrderedTrafficLight(val value: Int) extends IntEnumEntry

object OrderedTrafficLight
    extends CatsOrderValueEnum[Int, OrderedTrafficLight]
    with IntEnum[OrderedTrafficLight] {
  case object Red    extends OrderedTrafficLight(1)
  case object Yellow extends OrderedTrafficLight(2)
  case object Green  extends OrderedTrafficLight(3)

  val values = findValues
}
