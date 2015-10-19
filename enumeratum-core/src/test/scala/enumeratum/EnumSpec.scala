package enumeratum

import org.scalatest.{ Matchers, FunSpec }
import org.scalatest.OptionValues._

class EnumSpec extends FunSpec with Matchers {

  describe("no values") {

    it("should result in findValues finding nothing") {
      EmptyEnum.values shouldBe 'empty
    }

  }

  describe("when not wrapped in another object") {

    import DummyEnum._

    describe("#values") {

      it("should contain objects") {
        DummyEnum.values should be(IndexedSeq(Hello, GoodBye, Hi))
      }

    }

    describe("#withName") {

      it("should return the proper object when passed the proper string") {
        DummyEnum.withName("Hello") should be(Hello)
        DummyEnum.withName("GoodBye") should be(GoodBye)
        DummyEnum.withName("Hi") should be(Hi)
      }

      it("should throw an error otherwise") {
        intercept[NoSuchElementException] {
          DummyEnum.withName("hello")
        }
      }

    }

    describe("#withNameOption") {

      it("should return the proper object when passed the proper string") {
        DummyEnum.withNameOption("Hello").value should be(Hello)
        DummyEnum.withNameOption("GoodBye").value should be(GoodBye)
        DummyEnum.withNameOption("Hi").value should be(Hi)
      }

      it("should return None otherwise") {
        DummyEnum.withNameOption("hello") shouldBe None
      }

    }

    describe("#withNameInsensitive") {

      it("should return the proper object when passed the proper string, disregarding cases") {
        DummyEnum.withNameInsensitive("Hello") should be(Hello)
        DummyEnum.withNameInsensitive("hello") should be(Hello)
        DummyEnum.withNameInsensitive("GoodBye") should be(GoodBye)
        DummyEnum.withNameInsensitive("goodBye") should be(GoodBye)
        DummyEnum.withNameInsensitive("gOodbye") should be(GoodBye)
        DummyEnum.withNameInsensitive("Hi") should be(Hi)
        DummyEnum.withNameInsensitive("hI") should be(Hi)
      }

      it("should throw an error otherwise") {
        intercept[NoSuchElementException] {
          DummyEnum.withNameInsensitive("Hola")
        }
      }

    }

    describe("#withNameInsensitiveOption") {

      it("should return the proper object when passed the proper string, disregarding cases") {
        DummyEnum.withNameInsensitiveOption("Hello").value should be(Hello)
        DummyEnum.withNameInsensitiveOption("hello").value should be(Hello)
        DummyEnum.withNameInsensitiveOption("GoodBye").value should be(GoodBye)
        DummyEnum.withNameInsensitiveOption("goodBye").value should be(GoodBye)
        DummyEnum.withNameInsensitiveOption("gOodbye").value should be(GoodBye)
        DummyEnum.withNameInsensitiveOption("Hi").value should be(Hi)
        DummyEnum.withNameInsensitiveOption("hI").value should be(Hi)
      }

      it("should return None otherwise") {
        DummyEnum.withNameInsensitiveOption("bbeeeech") shouldBe None
      }

    }

  }

  describe("when a sealed trait is wrapped in another object") {

    import Wrapper._
    import Wrapper.SmartEnum._

    describe("#values") {

      it("should contain objects") {
        SmartEnum.values should be(IndexedSeq(Hello, GoodBye, Hi))
      }

    }

    describe("#withName") {

      it("should return the proper object when passed the proper string") {
        SmartEnum.withName("Hello") should be(Hello)
        SmartEnum.withName("GoodBye") should be(GoodBye)
        SmartEnum.withName("Hi") should be(Hi)
      }

      it("should throw an error otherwise") {
        intercept[NoSuchElementException] {
          SmartEnum.withName("hello")
        }
      }

    }

  }

  describe("when a sealed abstract class is wrapped in another object") {

    import InTheWoods.Mushroom._

    describe("#values") {

      it("should contain objects") {
        values shouldBe IndexedSeq(FlyAgaric, LSD, Shimeji)
      }

    }

    describe("#withName") {

      it("should return the proper object when passed the proper string") {
        withName("FlyAgaric") shouldBe FlyAgaric
        withName("LSD") shouldBe LSD
        withName("Shimeji") shouldBe Shimeji
      }

      it("should throw an error otherwise") {
        intercept[NoSuchElementException] {
          withName("hello")
        }
      }

    }

  }

  describe("when the entry has stackable traits mixed in") {

    describe("#withName") {
      it("should return the proper object when passed the proper string") {
        SnakeEnum.withName("hello") shouldBe SnakeEnum.Hello
        SnakeEnum.withName("good_bye") shouldBe SnakeEnum.GoodBye
        SnakeEnum.withName("SHOUT_GOOD_BYE") shouldBe SnakeEnum.ShoutGoodBye

        LowerEnum.withName("hello") shouldBe LowerEnum.Hello
        LowerEnum.withName("goodbye") shouldBe LowerEnum.GoodBye
        LowerEnum.withName("SIKE") shouldBe LowerEnum.Sike
      }
    }
  }

  describe("indexOf") {

    it("should return the proper index") {
      import DummyEnum._
      DummyEnum.indexOf(Hello) shouldBe 0
      DummyEnum.indexOf(GoodBye) shouldBe 1
      DummyEnum.indexOf(Hi) shouldBe 2
      import InTheWoods.Mushroom
      import InTheWoods.Mushroom._
      Mushroom.indexOf(FlyAgaric) shouldBe 0
      Mushroom.indexOf(LSD) shouldBe 1
      Mushroom.indexOf(Shimeji) shouldBe 2
      import Wrapper.SmartEnum
      SmartEnum.indexOf(SmartEnum.Hello) shouldBe 0
      SmartEnum.indexOf(SmartEnum.GoodBye) shouldBe 1
      SmartEnum.indexOf(SmartEnum.Hi) shouldBe 2
    }

  }

  describe("findValues Vector") {

    // This is a fairly intense test.
    it("should be in the same order that the objects were declared in") {
      import scala.util._
      (1 to 100).foreach { i =>
        val members = Random.shuffle((1 to Random.nextInt(20)).map { m => s"Member$m" })
        val membersDefs = members.map { m => s"case object $m extends Enum$i" }.mkString("\n\n")
        val objDefinition =
          s"""
            import enumeratum._
            sealed trait Enum$i extends EnumEntry

            case object Enum$i extends Enum[Enum$i] {
             $membersDefs
             val values = findValues
            }

            Enum$i
           """
        val obj = Eval[Enum[_ <: EnumEntry]](objDefinition)
        obj.values.map(_.entryName) shouldBe members
      }
    }

  }

  describe("trying to use with improper types") {

    it("should fail to compile for unsealed traits") {
      """
        trait NotSealed extends EnumEntry

        object NotSealed extends Enum[NotSealed] {
          val values = findValues
        }
      """ shouldNot compile
    }

    it("should fail to compile for unsealed abstract classes") {
      """
        abstract class Abstract extends EnumEntry

        object Abstract extends Enum[Abstract] {
          val values = findValues
        }
      """ shouldNot compile
    }

    it("should fail to compile for classes") {
      """
        class Class extends EnumEntry

        object Class extends Enum[Class] {
          val values = findValues
        }
      """ shouldNot compile
    }

    it("should fail to compile if the enum is not an object") {
      """
      sealed trait Foo extends EnumEntry

      class Class extends Enum[Foo] {
        val values = findValues

        case object Bar extends Foo
        case object Baz extends Foo
      }
        """ shouldNot compile
    }
  }

}
