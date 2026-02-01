package enumeratum

private[enumeratum] trait EnumSpecCompat { spec: EnumSpec =>
  def scalaCompat: Unit = describe("Scala3 in") {
    it(
      "should compile if either enum in the parameter list is not instance of the same enum type as the checked one"
    ) {
      val myEnum: DummyEnum = DummyEnum.Hi
      myEnum.in(DummyEnum.Hello, SnakeEnum.ShoutGoodBye) shouldBe false
    }

    describe("unsealed intermediate hierarchies") {
      it("should compile with warning and return empty values for unsealed trait intermediates") {
        // This demonstrates the known limitation: unsealed intermediates cause findValues to return empty
        // The warning helps users understand why their enum values are not being found
        sealed trait UnsealedBase  extends EnumEntry
        trait UnsealedIntermediate extends UnsealedBase // NOT sealed - causes warning

        case object UnsealedTestEnum extends Enum[UnsealedBase] {
          sealed abstract class Entry extends UnsealedIntermediate

          case object Value extends Entry

          lazy val values = findValues
        }

        // The limitation: values will be empty because UnsealedIntermediate is not sealed
        // The macro emits a warning to inform users about this known limitation
        UnsealedTestEnum.values shouldBe empty
      }

      it(
        "should compile with warning and return empty values for unsealed abstract class intermediates"
      ) {
        // This demonstrates the known limitation: unsealed intermediates cause findValues to return empty
        // The warning helps users understand why their enum values are not being found
        sealed trait UnsealedBase2 extends EnumEntry
        abstract class UnsealedIntermediateClass
            extends UnsealedBase2 // NOT sealed - causes warning

        case object UnsealedTestEnum2 extends Enum[UnsealedBase2] {
          sealed abstract class Entry extends UnsealedIntermediateClass

          case object Value1 extends Entry
          case object Value2 extends Entry

          lazy val values = findValues
        }

        // The limitation: values will be empty because UnsealedIntermediateClass is not sealed
        // The macro emits a warning to inform users about this known limitation
        UnsealedTestEnum2.values shouldBe empty
      }
    }
  }
}
