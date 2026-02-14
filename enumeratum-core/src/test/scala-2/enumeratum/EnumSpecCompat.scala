package enumeratum

// Test case for nested enum warning - defined at package level to ensure warning is emitted at compile time
class TestClassWithNestedEnum {
  sealed trait Foo extends EnumEntry
  object Foo extends Enum[Foo] {
    lazy val values = findValues
    case object a extends Foo
  }
}

private[enumeratum] trait EnumSpecCompat { _: EnumSpec =>
  def scalaCompat = describe("Scala2 in") {
    it(
      "should fail to compile if either enum in the parameter list is not instance of the same enum type as the checked one"
    ) {
      """
        val myEnum: DummyEnum = DummyEnum.Hi
        myEnum.in(DummyEnum.Hello, SnakeEnum.ShoutGoodBye)
      """ shouldNot compile
    }

    describe("enum nested inside a class") {
      it("should compile with warning and work correctly") {
        // This demonstrates that nesting enums inside classes works in Scala 2
        // but is discouraged because it won't work in Scala 3
        // Warning is emitted for TestClassWithNestedEnum defined at package level above
        val test = new TestClassWithNestedEnum()
        // In Scala 2, this works but emits a warning
        test.Foo.values should not be empty
        test.Foo.values.head.entryName shouldBe "a"
      }
    }
  }
}
