package enumeratum

private[enumeratum] trait EnumSpecCompat { _: EnumSpec =>
  describe("Scala2 in") {
    it(
      "should fail to compile if either enum in the parameter list is not instance of the same enum type as the checked one"
    ) {
      """
        val myEnum: DummyEnum = DummyEnum.Hi
        myEnum.in(DummyEnum.Hello, SnakeEnum.ShoutGoodBye)
      """ shouldNot compile
    }
  }
}
