package enumeratum

import enumeratum.UrlBinders._
import utest._

object UrlBindersTests extends TestSuite {

  val tests = TestSuite {

    ".pathBinder" - {

      val subject = pathBinder(Dummy)

      "binding proper strings" - assert(subject.bind("hello", "A") == Right(Dummy.A))

      "fail to bind improper strings" - assert(subject.bind("hello", "Z").isLeft)

      "unbinding" - {
        * - assert(subject.unbind("hello", Dummy.A) == "A")
        * - assert(subject.unbind("hello", Dummy.B) == "B")
      }

    }

    ".pathBinder case insensitive" - {

      val subject = pathBinder(Dummy, true)

      "binding strings, disregarding case" - {
        * - assert(subject.bind("hello", "A") == Right(Dummy.A))
        * - assert(subject.bind("hello", "a") == Right(Dummy.A))
        * - assert(subject.bind("hello", "B") == Right(Dummy.B))
        * - assert(subject.bind("hello", "b") == Right(Dummy.B))
      }

      "fail to bind improper strings not in the Enum" - {
        assert(subject.bind("hello", "Z").isLeft)
      }

      "unbinding values" - {
        * - assert(subject.unbind("hello", Dummy.A) == "A")
        * - assert(subject.unbind("hello", Dummy.B) == "B")
      }

    }

    ".queryBinder" - {

      val subject = queryBinder(Dummy)

      "binding strings" - {
        assert(subject.bind("hello", Map("hello" -> Seq("A"))) == Some(Right(Dummy.A)))
      }

      "fail to bind improper strings" - {
        * - assert(subject.bind("hello", Map("hello" -> Seq("Z"))).map(_.isLeft) == Some(true))
        * - assert(subject.bind("hello", Map("hello" -> Seq("a"))).map(_.isLeft) == Some(true))
        * - assert(subject.bind("hello", Map("helloz" -> Seq("A"))) == None)
      }

      "unbinding" - {
        * - assert(subject.unbind("hello", Dummy.A) == "hello=A")
        * - assert(subject.unbind("hello", Dummy.B) == "hello=B")
      }

    }

    ".queryBinder case insensitive" - {

      val subject = queryBinder(Dummy, true)

      "bind strings disregarding case" - {
        * - assert(subject.bind("hello", Map("hello" -> Seq("A"))) == Some(Right(Dummy.A)))
        * - assert(subject.bind("hello", Map("hello" -> Seq("a"))) == Some(Right(Dummy.A)))
        * - assert(subject.bind("hello", Map("hello" -> Seq("B"))) == Some(Right(Dummy.B)))
        * - assert(subject.bind("hello", Map("hello" -> Seq("b"))) == Some(Right(Dummy.B)))
      }

      "fail to bind strings not in the enum" - {
        * - assert(subject.bind("hello", Map("hello" -> Seq("Z"))).map(_.isLeft) == Some(true))
        * - assert(subject.bind("hello", Map("helloz" -> Seq("A"))) == None)
      }

      "unbinding" - {
        * - assert(subject.unbind("hello", Dummy.A) == "hello=A")
        * - assert(subject.unbind("hello", Dummy.B) == "hello=B")
      }

    }

  }

}
