package enumeratum

import enumeratum.Forms._
import play.api.data.Form
import utest._

object FormTests extends TestSuite {

  val tests = TestSuite {

    ".enum" - {

      val subject = Form("hello" -> enum(Dummy))

      "binding proper strings to enums should work" - {

        val r1 = subject.bind(Map("hello" -> "A"))
        val r2 = subject.bind(Map("hello" -> "B"))

        * - assert(r1.value == Some(Dummy.A))
        * - assert(r2.value == Some(Dummy.B))
      }

      "return None for random strings" - {

        val r = subject.bind(Map("hello" -> "AARSE"))

        * - assert(r.value == None)
      }

    }

    ".enum insensitive" - {

      val subject = Form("hello" -> enum(Dummy, true))

      "works with proper strings, disgarding case" - {
        val r1 = subject.bind(Map("hello" -> "A"))
        val r2 = subject.bind(Map("hello" -> "a"))
        val r3 = subject.bind(Map("hello" -> "B"))
        val r4 = subject.bind(Map("hello" -> "b"))

        * - assert(r1.value == Some(Dummy.A))
        * - assert(r2.value == Some(Dummy.A))
        * - assert(r3.value == Some(Dummy.B))
        * - assert(r4.value == Some(Dummy.B))
      }

      "fails on random strings" - {
        val r = subject.bind(Map("hello" -> "AARSE"))
        assert(r.value == None)
      }

    }

    ".format" - {

      val subject = format(Dummy)

      "binding proper strings into an Enum" - {

        val r1 = subject.bind("hello", Map("hello" -> "A"))
        val r2 = subject.bind("hello", Map("hello" -> "B"))

        * - assert(r1 == Right(Dummy.A))
        * - assert(r2 == Right(Dummy.B))
      }

      "fail to bind random strings" - {
        val r = subject.bind("hello", Map("hello" -> "AARSE"))
        assert(r.isLeft)
      }

      "unbinding" - {
        val r = subject.unbind("hello", Dummy.A)
        assert(r == Map("hello" -> "A"))
      }

    }

    ".format case insensitive" - {

      val subject = format(Dummy, true)

      "binding proper strings, disregarding case" - {

        val r1 = subject.bind("hello", Map("hello" -> "A"))
        val r2 = subject.bind("hello", Map("hello" -> "a"))
        val r3 = subject.bind("hello", Map("hello" -> "B"))
        val r4 = subject.bind("hello", Map("hello" -> "b"))

        * - assert(r1 == Right(Dummy.A))
        * - assert(r2 == Right(Dummy.A))
        * - assert(r3 == Right(Dummy.B))
        * - assert(r4 == Right(Dummy.B))
      }

      "fail to bind random strings" - {
        val r = subject.bind("hello", Map("hello" -> "AARSE"))
        assert(r.isLeft)
      }

      "unbind" - {
        val r = subject.unbind("hello", Dummy.A)
        assert(r == Map("hello" -> "A"))
      }

    }

  }

}
