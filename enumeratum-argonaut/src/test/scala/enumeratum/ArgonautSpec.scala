package enumeratum

import org.scalatest.{FunSpec, Matchers}

import argonaut._
import Argonaut._

/**
  * Created by alonsodomin on 14/10/2016.
  */
class ArgonautSpec extends FunSpec with Matchers {

  describe("to JSON") {
    it("should work") {
      TrafficLight.values.foreach { value =>
        value.asJson shouldBe value.entryName.asJson
      }
    }
  }

}
