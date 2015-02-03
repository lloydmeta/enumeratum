package enumeratum

import org.scalatest._

/**
 * Created by Lloyd on 2/3/15.
 */
class FormSpec extends FunSpec with Matchers {

  import Forms._

  sealed trait DummyEnum

  object DummyEnum extends Enum[DummyEnum] {
    case object A extends DummyEnum
    case object B extends DummyEnum
    val values = findValues
  }

  describe("binder from #enumFormat") {

    val subject = format(DummyEnum)

    it("should bind proper strings into an Enum value") {
      val r1 = subject.bind("hello", Map("hello" -> "A"))
      val r2 = subject.bind("hello", Map("hello" -> "B"))
      r1 shouldBe Right(DummyEnum.A)
      r2 shouldBe Right(DummyEnum.B)
    }

    it("should fail to bind random strings") {
      val r = subject.bind("hello", Map("hello" -> "AARSE"))
      r should be('left)
    }

  }

}
