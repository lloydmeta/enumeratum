package enumeratum

private[enumeratum] trait EnumSpecCompat { spec: EnumSpec =>
  def scalaCompat = describe("Scala3 in") {
    it(
      "should compile if either enum in the parameter list is not instance of the same enum type as the checked one"
    ) {
      val myEnum: DummyEnum = DummyEnum.Hi
      myEnum.in(DummyEnum.Hello, SnakeEnum.ShoutGoodBye) shouldBe false
    }
  }
}
