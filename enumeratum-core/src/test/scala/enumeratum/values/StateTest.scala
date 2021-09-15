package enumeratum.values

import org.scalatest.flatspec.AnyFlatSpec

class StateTest extends AnyFlatSpec {

  "State" should "return a correct entry name" in {

    val inputList =
      List(State.Alaska, State.Alabama)
    val outputList = List("AK", "AL")

    for ((input, expected) <- inputList.zip(outputList)) {
      val result = input.entryName
      assert(result == expected, f"for input {$input}")
    }

  }

}
