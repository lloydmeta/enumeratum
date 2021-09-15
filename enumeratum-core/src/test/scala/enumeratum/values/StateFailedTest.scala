package enumeratum.values

import org.scalatest.flatspec.AnyFlatSpec

class StateFailedTest extends AnyFlatSpec {

  "StateFailed" should "return a correct entry name" in {

    val inputList =
      List(StateFailed.Alaska, StateFailed.Alabama)
    val outputList = List("AK", "AL")

    for ((input, expected) <- inputList.zip(outputList)) {
      val result = input.entryName
      assert(result == expected, f"for input {$input}")
    }

  }

}
