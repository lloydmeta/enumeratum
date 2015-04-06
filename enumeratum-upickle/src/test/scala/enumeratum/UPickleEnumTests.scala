package enumeratum

import upickle._
import utest._

object UPickeEnumTests extends TestSuite {

  val tests = TestSuite {

    "deserialisation" - {

      'valid {
        * - assert(readJs[Dummy](Js.Str("A")) == Dummy.A)
      }

      'invalidStrings {
        * - intercept[Exception](readJs[Dummy](Js.Str("X")))
        * - intercept[Exception](readJs[Dummy](Js.Str("7")))
        * - intercept[Exception](readJs[Dummy](Js.Str("a")))
      }
    }

    'serialisation  {
      * - assert(writeJs(Dummy.A: Dummy) == Js.Str("A"))
    }
  }

}
