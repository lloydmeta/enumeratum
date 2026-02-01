package enumeratum

private[enumeratum] trait EnumSpecCompat { spec: EnumSpec =>
  def scalaCompat: Unit = describe("Scala3 in") {
    it(
      "should compile if either enum in the parameter list is not instance of the same enum type as the checked one"
    ) {
      val myEnum: DummyEnum = DummyEnum.Hi
      myEnum.in(DummyEnum.Hello, SnakeEnum.ShoutGoodBye) shouldBe false
    }

    it("should compile with warning for unsealed intermediate hierarchies") {
      """
        sealed trait BaseEntry extends EnumEntry

        object TestEnum extends Enum[BaseEntry] {
          trait UnsealedIntermediate extends BaseEntry  // NOT sealed - defined in companion
          sealed abstract class Entry extends UnsealedIntermediate

          case object Value extends Entry

          val values = findValues
        }
      """ should compile
    }

    it("should compile with warning for unsealed intermediate abstract classes") {
      """
        sealed trait BaseEntry extends EnumEntry

        object TestEnum extends Enum[BaseEntry] {
          abstract class UnsealedIntermediateClass extends BaseEntry  // NOT sealed
          sealed abstract class Entry extends UnsealedIntermediateClass

          case object Value1 extends Entry
          case object Value2 extends Entry

          val values = findValues
        }
      """ should compile
    }
  }
}
