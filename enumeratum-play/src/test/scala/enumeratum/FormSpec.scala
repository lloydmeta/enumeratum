package enumeratum

import org.scalatest._
import play.api.data.Form
import org.scalatest.OptionValues._

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
      r1.value.value shouldBe Dummy.A
      r2.value.value shouldBe Dummy.B
    }

    it("should fail to bind random strings") {
      val r = subject.bind(Map("hello" -> "AARSE"))
      r.value shouldBe None
    }

  }

  describe(".enum insensitive") {

    val subject = Form("hello" -> enum(Dummy, true))

    it("should bind proper strings into an Enum value disregarding case") {
      val r1 = subject.bind(Map("hello" -> "A"))
      val r2 = subject.bind(Map("hello" -> "a"))
      val r3 = subject.bind(Map("hello" -> "B"))
      val r4 = subject.bind(Map("hello" -> "b"))
      r1.value.value shouldBe Dummy.A
      r2.value.value shouldBe Dummy.A
      r3.value.value shouldBe Dummy.B
      r4.value.value shouldBe Dummy.B
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

  describe(".format case insensitive") {

    val subject = format(Dummy, true)

    it("should bind proper strings into an Enum value") {
      val r1 = subject.bind("hello", Map("hello" -> "A"))
      val r2 = subject.bind("hello", Map("hello" -> "a"))
      val r3 = subject.bind("hello", Map("hello" -> "B"))
      val r4 = subject.bind("hello", Map("hello" -> "b"))
      r1 shouldBe Right(Dummy.A)
      r2 shouldBe Right(Dummy.A)
      r3 shouldBe Right(Dummy.B)
      r4 shouldBe Right(Dummy.B)
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
