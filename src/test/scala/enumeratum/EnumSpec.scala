package enumeratum

import org.scalatest.{ Matchers, FunSpec }

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
        intercept[IllegalArgumentException] {
          DummyEnum.withName("hello")
        }
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
        intercept[IllegalArgumentException] {
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
        intercept[IllegalArgumentException] {
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
