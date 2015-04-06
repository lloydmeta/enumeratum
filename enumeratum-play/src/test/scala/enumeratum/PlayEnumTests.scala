package enumeratum

import play.api.data.Form
import play.api.libs.json.{ Json => PlayJson, JsNumber, JsString }
import utest._

object PlayEnumTests extends TestSuite {

  import PlayDummy._

  val tests = TestSuite {

    "JSON serdes" - {

      "deserialisation" - {

        "with valid values" - {
          assert(JsString("A").asOpt[PlayDummy] == Some(A))
        }

        "with invalid values" - {
          * - assert(JsString("D").asOpt[PlayDummy] == None)
          * - assert(JsNumber(2).asOpt[PlayDummy] == None)
        }

      }

      "serialisation" - {
        * - assert(PlayJson.toJson(A) == JsString("A"))
      }

    }

    "form binding" - {

      val subject = Form("hello" -> PlayDummy.formField)

      "binding proper strings" - {
        val r1 = subject.bind(Map("hello" -> "A"))
        val r2 = subject.bind(Map("hello" -> "B"))
        * - assert(r1.value == Some(A))
        * - assert(r2.value == Some(B))
      }

      "fail to bind random strings" - {
        assert(subject.bind(Map("hello" -> "AARSE")).value == None)
      }

    }

    "URL binding" - {

      "PathBindable" - {

        val subject = PlayDummy.pathBindable

        "binding proper strings" - {
          * - assert(subject.bind("hello", "A") == Right(A))
          * - assert(subject.bind("hello", "B") == Right(B))
        }

        "fail to bind improper strings" - {
          assert(subject.bind("hello", "Z").isLeft)
        }

        "unbind values" - {
          * - assert(subject.unbind("hello", A) == "A")
          * - assert(subject.unbind("hello", B) == "B")
        }

      }

      "QueryStringBindable" - {

        val subject = PlayDummy.queryBindable

        "binding strings" - {
          assert(subject.bind("hello", Map("hello" -> Seq("A"))) == Some(Right(A)))
        }

        "fail to bind random improper strings" - {
          assert(subject.bind("hello", Map("hello" -> Seq("Z"))).map(_.isLeft) == Some(true))
          assert(subject.bind("hello", Map("helloz" -> Seq("A"))).map(_.isLeft) == None)
        }

        "unbind values" - {
          * - assert(subject.unbind("hello", A) == "hello=A")
          * - assert(subject.unbind("hello", B) == "hello=B")
        }

      }

    }

  }

}
