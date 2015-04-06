package enumeratum

import upickle._
import utest._

object UPickerTests extends TestSuite {

  val tests = TestSuite {

    import Dummy._

    "Reader" - {

      val reader = UPickler.reader(Dummy)

      "works with valid values" - {
        assert(reader.read(Js.Str("A")) == A)
      }

      "fails with invalid values" - {
        * - intercept[Exception](reader.read(Js.Str("D")))
        * - intercept[Exception](reader.read(Js.Num(2)))
      }

    }

    "reads insensitive" - {

      val reader = UPickler.reader(Dummy, true)

      "works with strings, disgregarding case" - {
        * - assert(reader.read(Js.Str("A")) == A)
        * - assert(reader.read(Js.Str("a")) == A)
      }

      "fails with invalid values" - {
        * - intercept[Exception](reader.read(Js.Str("D")))
        * - intercept[Exception](reader.read(Js.Num(2)))
      }

    }

    "writes" - {

      val writer = UPickler.writer(Dummy)

      "writes enum values to JsString" - {
        assert(writer.write(A) == Js.Str("A"))
      }

    }

  }

}
