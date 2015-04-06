package enumeratum

import upickle._
import utest._

object UPickleEnumTests extends TestSuite {

  val tests = TestSuite {

    import Dummy._

    "deserialisation" - {

      'valid {
        * - assert(read[Dummy](""""A"""") == A)
        * - assert(read[Dummy](""""B"""") == B)
        * - assert(readJs[Dummy](Js.Str("A")) == A)
        * - assert(readJs[Dummy](Js.Str("B")) == B)
      }

      'invalidStrings {
        * - intercept[Exception](readJs[Dummy](Js.Str("D"))) // not part of the enum
        * - intercept[Exception](readJs[Dummy](Js.Str("X")))
        * - intercept[Exception](readJs[Dummy](Js.Str("7")))
        * - intercept[Exception](readJs[Dummy](Js.Str("a")))
        * - intercept[Exception](read[Dummy](""""D""""))
        * - intercept[Exception](read[Dummy](""""n""""))
      }
    }

    'serialisation  {
      * - assert(writeJs(A: Dummy) == Js.Str("A"))
      * - assert(write(A: Dummy) == """"A"""")
    }
  }

}
