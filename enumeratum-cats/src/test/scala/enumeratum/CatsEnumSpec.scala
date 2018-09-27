package enumeratum

import cats.{Eq, Hash, Show}
import cats.syntax.eq._
import cats.syntax.show._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, FreeSpec, Matchers}

class CatsEnumSpec extends FreeSpec with ScalaFutures with Matchers with BeforeAndAfterAll {

  "CatsEnum" - {
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
        trafficLight.show shouldBe TrafficLight.Red.entryName
      }
    }
    "has a Hash instance" - {
      val _ = implicitly[Hash[TrafficLight]]
    }
  }
}

sealed trait TrafficLight extends EnumEntry
object TrafficLight extends Enum[TrafficLight] with CatsEnum[TrafficLight] {
  case object Red    extends TrafficLight
  case object Yellow extends TrafficLight
  case object Green  extends TrafficLight

  val values = findValues
}
