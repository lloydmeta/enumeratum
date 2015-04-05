package enumeratum

import org.scalatest.{ Matchers, FunSpec }
import org.scalatest.OptionValues._

class EnumSpec extends FunSpec with Matchers {

  describe("when not wrapped in another object") {

    import DummyEnum._

    describe("#values") {

      it("should contain objects") {
        DummyEnum.values should be(Set(Hello, GoodBye, Hi))
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
        SmartEnum.values should be(Set(Hello, GoodBye, Hi))
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
        values shouldBe Set(FlyAgaric, LSD, Shimeji)
      }

    }

    describe("#withName") {

      it("should return the proper object when passed the proper string") {
        withName("FlyAgaric") should be(FlyAgaric)
        withName("LSD") should be(LSD)
        withName("Shimeji") should be(Shimeji)
      }

      it("should throw an error otherwise") {
        intercept[NoSuchElementException] {
          withName("hello")
        }
      }

    }

  }

  describe("trying to use with improper types") {

    it("should fail to compile for unsealed traits") {
      """
        trait NotSealed

        object NotSealed extends Enum[NotSealed] {
          val values = findValues
        }
      """ shouldNot compile
    }

    it("should fail to compile for unsealed abstract classes") {
      """
        abstract class Abstract

        object Abstract extends Enum[Abstract] {
          val values = findValues
        }
      """ shouldNot compile
    }

    it("should fail to compile for classes") {
      """
        class Class

        object Class extends Enum[Class] {
          val values = findValues
        }
      """ shouldNot compile
    }

    it("should fail to compile if the enum is not an object") {
      """
      sealed trait Foo

      class Class extends Enum[Foo] {
        val values = findValues

        case object Bar extends Foo
        case object Baz extends Foo
      }
        """ shouldNot compile
    }
  }

}
