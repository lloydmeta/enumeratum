package enumeratum

import play.api.libs.json.{ JsNumber, JsString, Json => PlayJson }
import utest._

object PlayJsonEnumTests extends TestSuite {

  val tests = TestSuite {

    "deserialisation" - {

      'valid - assert(JsString("A").as[Dummy] == Dummy.A)

      'invalidStrings{
        * - assert(JsString("D").asOpt[Dummy] == None)
        * - assert(JsNumber(2).asOpt[Dummy] == None)
      }
    }

    'serialisation - assert(PlayJson.toJson(Dummy.A) == JsString("A"))
  }

}
