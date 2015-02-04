package enumeratum

import org.scalatest._
import play.api.data.Form

/**
 * Created by Lloyd on 2/3/15.
 */
class FormSpec extends FunSpec with Matchers {

  import Forms._

  describe(".enum") {

    val subject = Form("hello" -> enum(Dummy))

    it("should bind proper strings into an Enum value") {
      val r1 = subject.bind(Map("hello" -> "A"))
      val r2 = subject.bind(Map("hello" -> "B"))
      r1.value.get shouldBe Dummy.A
      r2.value.get shouldBe Dummy.B
    }

    it("should fail to bind random strings") {
      val r = subject.bind(Map("hello" -> "AARSE"))
      r.value shouldBe None
    }

  }

  describe(".format") {

    val subject = format(Dummy)

    it("should bind proper strings into an Enum value") {
      val r1 = subject.bind("hello", Map("hello" -> "A"))
      val r2 = subject.bind("hello", Map("hello" -> "B"))
      r1 shouldBe Right(Dummy.A)
      r2 shouldBe Right(Dummy.B)
    }

    it("should fail to bind random strings") {
      val r = subject.bind("hello", Map("hello" -> "AARSE"))
      r should be('left)
    }

    it("should unbind ") {
      val r = subject.unbind("hello", Dummy.A)
      r shouldBe Map("hello" -> "A")
    }

  }

}
