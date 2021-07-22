package enumeratum.values

import cats.syntax.eq._
import cats.syntax.show._
import cats.{Eq, Show}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class CatsValueEnumSpec extends AnyFreeSpec with ScalaFutures with Matchers {

  "CatsValueEnum" - {
    "has a proper Eq instance" - {
      val eq = implicitly[Eq[TrafficLight]]
      "Eq works for equal values" in {
        // === doesn't work as Scalatest has its own variant
        assert(eq.eqv(TrafficLight.Red, TrafficLight.Red))
        assert(eq.eqv(TrafficLight.Yellow, TrafficLight.Yellow))
        assert(eq.eqv(TrafficLight.Green, TrafficLight.Green))
      }
      "Eq works for non-equal values" in {
        assert((TrafficLight.Red: TrafficLight) =!= (TrafficLight.Yellow: TrafficLight))
        assert((TrafficLight.Yellow: TrafficLight) =!= (TrafficLight.Green: TrafficLight))
        assert((TrafficLight.Green: TrafficLight) =!= (TrafficLight.Red: TrafficLight))
      }
    }
    "has a proper Show instance" - {
      val _ = implicitly[Show[TrafficLight]]
      "it returns the entry's name" in {
        val trafficLight: TrafficLight = TrafficLight.Red
        trafficLight.show shouldBe "Red"
      }
    }
  }
}

sealed abstract class TrafficLight(val value: Int) extends IntEnumEntry
object TrafficLight extends IntEnum[TrafficLight] with CatsValueEnum[Int, TrafficLight] {
  case object Red    extends TrafficLight(1)
  case object Yellow extends TrafficLight(2)
  case object Green  extends TrafficLight(3)

  val values = findValues
}
