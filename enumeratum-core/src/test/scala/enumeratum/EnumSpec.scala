package enumeratum

import org.scalatest.OptionValues._
import org.scalatest.{FunSpec, Matchers}

class EnumSpec extends FunSpec with Matchers {

  describe("no values") {

    it("should result in findValues finding nothing") {
      EmptyEnum.values.size shouldBe 0
    }

  }

  describe("when not wrapped in another object") {

    import DummyEnum._

    describe("#values") {

      it("should contain objects") {
        DummyEnum.values should be(IndexedSeq(Hello, GoodBye, Hi))
      }

      it("should contain objects found in nested objects") {
        NestedObjectEnum.values should be(
          IndexedSeq(
            NestedObjectEnum.Hello,
            NestedObjectEnum.GoodBye,
            NestedObjectEnum.nested1.Hello,
            NestedObjectEnum.nested1.GoodBye,
            NestedObjectEnum.nested1.GoodBye.NestedInsideMember,
            NestedObjectEnum.nested2.Hello,
            NestedObjectEnum.nested2.GoodBye,
            NestedObjectEnum.nested2.nested3.Hello
          ))
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

    describe("#withNameEither") {

      it("should return the proper object when passed the proper string") {
        DummyEnum.withNameEither("Hello") should be(Right(Hello))
        DummyEnum.withNameEither("GoodBye") should be(Right(GoodBye))
        DummyEnum.withNameEither("Hi") should be(Right(Hi))
      }

      it("should return Left otherwise") {
        DummyEnum.withNameEither("hello") shouldBe Left(NoSuchMember("hello", DummyEnum.values))
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

    describe("#withNameInsensitiveEither") {

      it("should return the proper object when passed the proper string, disregarding cases") {
        DummyEnum.withNameInsensitiveEither("Hello") should be(Right(Hello))
        DummyEnum.withNameInsensitiveEither("hello") should be(Right(Hello))
        DummyEnum.withNameInsensitiveEither("GoodBye") should be(Right(GoodBye))
        DummyEnum.withNameInsensitiveEither("goodBye") should be(Right(GoodBye))
        DummyEnum.withNameInsensitiveEither("gOodbye") should be(Right(GoodBye))
        DummyEnum.withNameInsensitiveEither("Hi") should be(Right(Hi))
        DummyEnum.withNameInsensitiveEither("hI") should be(Right(Hi))
      }

      it("should return Left otherwise") {
        DummyEnum.withNameInsensitiveEither("bbeeeech") shouldBe Left(
          NoSuchMember("bbeeeech", DummyEnum.values))
      }

    }

    describe("#withNameUppercaseOnly") {
      it("should return the proper object when passed the proper string, transforming to upper case first") {
        DummyEnum.withNameUppercaseOnly("HELLO") should be(Hello)
        DummyEnum.withNameUppercaseOnly("GOODBYE") should be(GoodBye)
        DummyEnum.withNameUppercaseOnly("HI") should be(Hi)
      }

      it("should throw an error for not uppercase but case insensitive values") {
        intercept[NoSuchElementException] {
          DummyEnum.withNameUppercaseOnly("Hello")
        }
        intercept[NoSuchElementException] {
          DummyEnum.withNameUppercaseOnly("GoodBye")
        }
        intercept[NoSuchElementException] {
          DummyEnum.withNameUppercaseOnly("Hi")
        }
      }

      it("should throw an error otherwise") {
        intercept[NoSuchElementException] {
          DummyEnum.withNameUppercaseOnly("bbeeeech")
        }
      }
    }

    describe("#withNameUppercaseOnlyOption") {
      it("should return the proper object when passed the proper string, transforming to upper case first") {
        DummyEnum.withNameUppercaseOnlyOption("HELLO").value should be(Hello)
        DummyEnum.withNameUppercaseOnlyOption("GOODBYE").value should be(GoodBye)
        DummyEnum.withNameUppercaseOnlyOption("HI").value should be(Hi)
      }

      it("should return None for not uppercase but case insensitive values") {
        DummyEnum.withNameUppercaseOnlyOption("Hello") should be(None)
        DummyEnum.withNameUppercaseOnlyOption("GoodBye") should be(None)
        DummyEnum.withNameUppercaseOnlyOption("Hi") should be(None)
      }

      it("should return None otherwise") {
        DummyEnum.withNameUppercaseOnlyOption("bbeeeech") should be(None)
      }
    }

    describe("#withNameUppercaseOnlyEither") {
      it("should return the proper object when passed the proper string, transforming to upper case first") {
        DummyEnum.withNameUppercaseOnlyEither("HELLO") should be(Right(Hello))
        DummyEnum.withNameUppercaseOnlyEither("GOODBYE") should be(Right(GoodBye))
        DummyEnum.withNameUppercaseOnlyEither("HI") should be(Right(Hi))
      }

      it("should return Left for not uppercase but case insensitive values") {
        DummyEnum.withNameUppercaseOnlyEither("Hello") should be(
          Left(NoSuchMember("Hello", DummyEnum.values)))
        DummyEnum.withNameUppercaseOnlyEither("GoodBye") should be(
          Left(NoSuchMember("GoodBye", DummyEnum.values)))
        DummyEnum.withNameUppercaseOnlyEither("Hi") should be(
          Left(NoSuchMember("Hi", DummyEnum.values)))
      }

      it("should return Left otherwise") {
        DummyEnum.withNameUppercaseOnlyEither("bbeeeech") should be(
          Left(NoSuchMember("bbeeeech", DummyEnum.values)))
      }
    }

    describe("#withNameLowercaseOnly") {
      it("should return the proper object when passed the proper string, transforming to lower case first") {
        DummyEnum.withNameLowercaseOnly("hello") should be(Hello)
        DummyEnum.withNameLowercaseOnly("goodbye") should be(GoodBye)
        DummyEnum.withNameLowercaseOnly("hi") should be(Hi)
      }

      it("should throw an error for not lowercase but case insensitive values") {
        intercept[NoSuchElementException] {
          DummyEnum.withNameLowercaseOnly("Hello")
        }
        intercept[NoSuchElementException] {
          DummyEnum.withNameLowercaseOnly("GoodBye")
        }
        intercept[NoSuchElementException] {
          DummyEnum.withNameLowercaseOnly("Hi")
        }
      }

      it("should throw an error otherwise") {
        intercept[NoSuchElementException] {
          DummyEnum.withNameLowercaseOnly("bbeeeech")
        }
      }
    }

    describe("#withNameLowercaseOnlyOption") {
      it("should return the proper object when passed the proper string, transforming to lower case first") {
        DummyEnum.withNameLowercaseOnlyOption("hello").value should be(Hello)
        DummyEnum.withNameLowercaseOnlyOption("goodbye").value should be(GoodBye)
        DummyEnum.withNameLowercaseOnlyOption("hi").value should be(Hi)
      }

      it("should return None for not lowercase but case insensitive values") {
        DummyEnum.withNameLowercaseOnlyOption("Hello") should be(None)
        DummyEnum.withNameLowercaseOnlyOption("GoodBye") should be(None)
        DummyEnum.withNameLowercaseOnlyOption("Hi") should be(None)
      }

      it("should return None otherwise") {
        DummyEnum.withNameLowercaseOnlyOption("bbeeeech") should be(None)
      }
    }

    describe("#withNameLowercaseOnlyEither") {
      it("should return the proper object when passed the proper string, transforming to lower case first") {
        DummyEnum.withNameLowercaseOnlyEither("hello") should be(Right(Hello))
        DummyEnum.withNameLowercaseOnlyEither("goodbye") should be(Right(GoodBye))
        DummyEnum.withNameLowercaseOnlyEither("hi") should be(Right(Hi))
      }

      it("should return Left for not lowercase but case insensitive values") {
        DummyEnum.withNameLowercaseOnlyEither("Hello") should be(
          Left(NoSuchMember("Hello", DummyEnum.values)))
        DummyEnum.withNameLowercaseOnlyEither("GoodBye") should be(
          Left(NoSuchMember("GoodBye", DummyEnum.values)))
        DummyEnum.withNameLowercaseOnlyEither("Hi") should be(
          Left(NoSuchMember("Hi", DummyEnum.values)))
      }

      it("should return Left otherwise") {
        DummyEnum.withNameLowercaseOnlyEither("bbeeeech") should be(
          Left(NoSuchMember("bbeeeech", DummyEnum.values)))
      }
    }
  }

  describe("when a sealed trait is wrapped in another object") {

    import Wrapper.SmartEnum._
    import Wrapper._

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

        UpperSnakeEnum.withName("HELLO") shouldBe UpperSnakeEnum.Hello
        UpperSnakeEnum.withName("GOOD_BYE") shouldBe UpperSnakeEnum.GoodBye
        UpperSnakeEnum.withName("whisper_good_bye") shouldBe UpperSnakeEnum.WhisperGoodBye

        CapitalSnakeEnum.withName("Hello") shouldBe CapitalSnakeEnum.Hello
        CapitalSnakeEnum.withName("Good_Bye") shouldBe CapitalSnakeEnum.GoodBye
        CapitalSnakeEnum.withName("whisper_good_bye") shouldBe CapitalSnakeEnum.WhisperGoodBye

        HyphenEnum.withName("hello") shouldBe HyphenEnum.Hello
        HyphenEnum.withName("good-bye") shouldBe HyphenEnum.GoodBye
        HyphenEnum.withName("SHOUT-GOOD-BYE") shouldBe HyphenEnum.ShoutGoodBye

        UpperHyphenEnum.withName("HELLO") shouldBe UpperHyphenEnum.Hello
        UpperHyphenEnum.withName("GOOD-BYE") shouldBe UpperHyphenEnum.GoodBye
        UpperHyphenEnum.withName("whisper-good-bye") shouldBe UpperHyphenEnum.WhisperGoodBye

        CapitalHyphenEnum.withName("Hello") shouldBe CapitalHyphenEnum.Hello
        CapitalHyphenEnum.withName("Good-Bye") shouldBe CapitalHyphenEnum.GoodBye
        CapitalHyphenEnum.withName("whisper-good-bye") shouldBe CapitalHyphenEnum.WhisperGoodBye

        DotEnum.withName("hello") shouldBe DotEnum.Hello
        DotEnum.withName("good.bye") shouldBe DotEnum.GoodBye
        DotEnum.withName("SHOUT.GOOD.BYE") shouldBe DotEnum.ShoutGoodBye

        UpperDotEnum.withName("HELLO") shouldBe UpperDotEnum.Hello
        UpperDotEnum.withName("GOOD.BYE") shouldBe UpperDotEnum.GoodBye
        UpperDotEnum.withName("whisper.good.bye") shouldBe UpperDotEnum.WhisperGoodBye

        CapitalDotEnum.withName("Hello") shouldBe CapitalDotEnum.Hello
        CapitalDotEnum.withName("Good.Bye") shouldBe CapitalDotEnum.GoodBye
        CapitalDotEnum.withName("whisper.good.bye") shouldBe CapitalDotEnum.WhisperGoodBye

        WordsEnum.withName("hello") shouldBe WordsEnum.Hello
        WordsEnum.withName("good bye") shouldBe WordsEnum.GoodBye
        WordsEnum.withName("SHOUT GOOD BYE") shouldBe WordsEnum.ShoutGoodBye

        UpperWordsEnum.withName("HELLO") shouldBe UpperWordsEnum.Hello
        UpperWordsEnum.withName("GOOD BYE") shouldBe UpperWordsEnum.GoodBye
        UpperWordsEnum.withName("whisper good bye") shouldBe UpperWordsEnum.WhisperGoodBye

        CapitalWordsEnum.withName("Hello") shouldBe CapitalWordsEnum.Hello
        CapitalWordsEnum.withName("Good Bye") shouldBe CapitalWordsEnum.GoodBye
        CapitalWordsEnum.withName("whisper good bye") shouldBe CapitalWordsEnum.WhisperGoodBye

        UpperEnum.withName("HELLO") shouldBe UpperEnum.Hello
        UpperEnum.withName("GOODBYE") shouldBe UpperEnum.GoodBye
        UpperEnum.withName("sike") shouldBe UpperEnum.Sike

        LowerEnum.withName("hello") shouldBe LowerEnum.Hello
        LowerEnum.withName("goodbye") shouldBe LowerEnum.GoodBye
        LowerEnum.withName("SIKE") shouldBe LowerEnum.Sike

        CamelcaseEnum.withName("Hello") shouldBe CamelcaseEnum.HELLO
        CamelcaseEnum.withName("GoodBye") shouldBe CamelcaseEnum.GOOD_BYE
        CamelcaseEnum.withName("sikeagain") shouldBe CamelcaseEnum.SIKE_AGAIN
        CamelcaseEnum.withName("Private") shouldBe CamelcaseEnum._PRIVATE
        CamelcaseEnum.withName("CamelCase") shouldBe CamelcaseEnum.CamelCase

        LowerCamelcaseEnum.withName("hello") shouldBe LowerCamelcaseEnum.HELLO
        LowerCamelcaseEnum.withName("goodBye") shouldBe LowerCamelcaseEnum.GOOD_BYE
        LowerCamelcaseEnum.withName("SIKEAGAIN") shouldBe LowerCamelcaseEnum.SIKE_AGAIN
        LowerCamelcaseEnum.withName("private") shouldBe LowerCamelcaseEnum._PRIVATE

        UncapitalisedEnum.withName("hello") shouldBe UncapitalisedEnum.Hello
        UncapitalisedEnum.withName("goodBye") shouldBe UncapitalisedEnum.GoodBye
        UncapitalisedEnum.withName("SIKE") shouldBe UncapitalisedEnum.Sike
        UncapitalisedEnum.withName("a") shouldBe UncapitalisedEnum.a

        MultiEnum.withName("one") shouldBe MultiEnum.One
        MultiEnum.withName("1") shouldBe MultiEnum.One
        a[NoSuchElementException] should be thrownBy MultiEnum.withName("One")
        MultiEnum.withName("eins") shouldBe MultiEnum.One
        MultiEnum.withName("two") shouldBe MultiEnum.Two
      }
    }
  }

  describe("indexOf") {

    it("should return the proper index for elements that exist in values") {
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

    it("should return -1 for elements that do not exist") {
      // Does this even make sense given that we need to have sealed traits/classes ??
      sealed trait Reactions extends EnumEntry
      case object Reactions extends Enum[Reactions] {

        case object Blah extends Reactions

        case object Yay extends Reactions

        case object Nay extends Reactions

        val values = findValues
      }
      case object Woot extends Reactions
      Reactions.indexOf(Woot) shouldBe -1
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

  describe("in") {
    import DummyEnum._

    it("should return true if enum value is contained by the parameter list") {
      val enum: DummyEnum = Hello
      enum.in(Hello, GoodBye) should be(true)
    }

    it("should return false if enum value is not contained by the parameter list") {
      val enum: DummyEnum = Hi
      enum.in(Hello, GoodBye) should be(false)
    }

    it(
      "should fail to compile if either enum in the parameter list is not instance of the same enum type as the checked one"
    ) {
      """
        val enum: DummyEnum = DummyEnum.Hi
        enum.in(DummyEnum.Hello, SnakeEnum.ShoutGoodBye)
      """ shouldNot compile
    }
  }

  describe("materializeEnum") {
    import DummyEnum._
    def findEnum[A <: EnumEntry: Enum](v: A) = implicitly[Enum[A]]

    it("should return the proper Enum object") {
      val hello: DummyEnum = Hello
      val companion        = findEnum(hello)
      companion shouldBe DummyEnum
      companion.values should contain(Hello)
    }

    it("should fail to compile when passed a singleton object") {
      """
        | findEnum(Hello)
      """ shouldNot compile
    }
  }
}
