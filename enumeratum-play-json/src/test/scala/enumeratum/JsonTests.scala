package enumeratum

import play.api.libs.json.{ JsNumber, JsString }
import utest._

object JsonTests extends TestSuite {

  val tests = TestSuite {

    "reads" - {

      val reads = Json.reads(Dummy)

      "works with valid values" - {
        assert(reads.reads(JsString("A")).asOpt == Some(Dummy.A))
      }

      "fails with invalid values" - {
        * - assert(reads.reads(JsString("D")).isError)
        * - assert(reads.reads(JsNumber(2)).isError)
      }

    }

    "reads insensitive" - {

      val reads = Json.reads(Dummy, true)

      "works with strings, disgregarding case" - {
        * - assert(reads.reads(JsString("A")).asOpt == Some(Dummy.A))
        * - assert(reads.reads(JsString("a")).asOpt == Some(Dummy.A))
      }

      "fails with invalid values" - {
        * - assert(reads.reads(JsString("D")).isError)
        * - assert(reads.reads(JsNumber(2)).isError)
      }

    }

    "writes" - {

      val writer = Json.writes(Dummy)

      "writes enum values to JsString" - {
        assert(writer.writes(Dummy.A) == JsString("A"))
      }

    }

    "formats" - {
      val format = Json.formats(Dummy)

      "reads valid values" - {
        assert(format.reads(JsString("A")).asOpt == Some(Dummy.A))
      }

      "fails to read invalid values" - {
        * - assert(format.reads(JsString("D")).isError)
        * - assert(format.reads(JsNumber(2)).isError)
      }

      "writes enum values to JsString" - {
        assert(format.writes(Dummy.A) == JsString("A"))
      }
    }
  }

}
