package enumeratum
import utest._

object EnumTests extends TestSuite {

  val tests = TestSuite {

    "when not wrapped in another object" - {

      import DummyEnum._

      "#values" - {

        "has all the objects of the enum" - {
          assert(DummyEnum.values == Set(Hello, GoodBye, Hi))
        }

      }

      "#withName" - {

        "with a supported string" - {
          * - assert(DummyEnum.withName("Hello") == Hello)
          * - assert(DummyEnum.withName("GoodBye") == GoodBye)
          * - assert(DummyEnum.withName("Hi") == Hi)
        }

        "with an improper string" - {
          'throws - {
            intercept[NoSuchElementException] {
              DummyEnum.withName("hello")
            }
          }
        }

      }

      "#withNameOption" - {

        "with a proper string" - {
          * - assert(DummyEnum.withNameOption("Hello") == Some(Hello))
          * - assert(DummyEnum.withNameOption("GoodBye") == Some(GoodBye))
          * - assert(DummyEnum.withNameOption("Hi") == Some(Hi))
        }

        "with an imporoper string" - {
          * - assert(DummyEnum.withNameOption("hello") == None)
        }

      }

      "#withNameInsensitiveOption" - {

        "disregard case of strings" - {
          * - assert(DummyEnum.withNameInsensitiveOption("Hello") == Some(Hello))
          * - assert(DummyEnum.withNameInsensitiveOption("hello") == Some(Hello))
          * - assert(DummyEnum.withNameInsensitiveOption("GoodBye") == Some(GoodBye))
          * - assert(DummyEnum.withNameInsensitiveOption("goodBye") == Some(GoodBye))
          * - assert(DummyEnum.withNameInsensitiveOption("gOodbye") == Some(GoodBye))
          * - assert(DummyEnum.withNameInsensitiveOption("Hi") == Some(Hi))
          * - assert(DummyEnum.withNameInsensitiveOption("hI") == Some(Hi))
        }

        "returns None if there is no case insensitive match" - {
          assert(DummyEnum.withNameInsensitiveOption("bbeeeech") == None)
        }


      }

    }

    "when a sealed trait is wrapped in another object" - {
      import Wrapper._
      import Wrapper.SmartEnum._

      "#values" - {

        "has all the objects in the enum" - {
          assert(SmartEnum.values == Set(Hello, GoodBye, Hi))
        }

      }

      "#withName" - {

        "when passed proper strings" - {
          * - assert(SmartEnum.withName("Hello") == Hello)
          * - assert(SmartEnum.withName("GoodBye") == GoodBye)
          * - assert(SmartEnum.withName("Hi") == Hi)
        }

        "passed improper strings" - {
          'throws - {
            intercept[NoSuchElementException] {
              SmartEnum.withName("hello")
            }
          }
        }

      }
    }

    "when a sealed abstract class is wrapped in another object" - {

      import InTheWoods.Mushroom._

      "#values" - {

        "contains all the objects in the enum" - {
          assert(values == Set(FlyAgaric, LSD, Shimeji))
        }

      }

      "#withName" - {

        "when passed proper strings" - {
          * - assert(withName("FlyAgaric") == FlyAgaric)
          * - assert(withName("LSD") == LSD)
          * - assert(withName("Shimeji") == Shimeji)
        }

        "when passed inproper strings" - {
          'throws - {
            intercept[NoSuchElementException] {
              withName("hello")
            }
          }
        }

      }

    }

    "trying to use with improper types should fail compilation" - {

      * - compileError("""
        trait NotSealed

        object NotSealed extends Enum[NotSealed] {
          val values = findValues
        }
                       """)

      * - compileError("""
        abstract class Abstract

        object Abstract extends Enum[Abstract] {
          val values = findValues
        }
                       """)

      * - compileError("""
        class Class

        object Class extends Enum[Class] {
          val values = findValues
        }
                       """ )

      * - compileError("""
      sealed trait Foo

      class Class extends Enum[Foo] {
        val values = findValues

        case object Bar extends Foo
        case object Baz extends Foo
      }
                       """)

    }

  }
}
