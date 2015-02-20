package enumeratum

import UrlBinders._
import org.scalatest._
import org.scalatest.OptionValues._
import org.scalatest.EitherValues._

/**
 * Created by Lloyd on 2/3/15.
 */
class UrlBindersSpec extends FunSpec with Matchers {

  describe(".pathBinder") {

    val subject = pathBinder(Dummy)

    it("should create an enumeration binder that can bind strings corresponding to enum strings") {
      subject.bind("hello", "A").right.value shouldBe Dummy.A
    }

    it("should create an enumeration binder that cannot bind strings not found in the enumeration") {
      subject.bind("hello", "Z").isLeft shouldBe true
    }

    it("should create an enumeration binder that can unbind values") {
      subject.unbind("hello", Dummy.A) shouldBe "A"
      subject.unbind("hello", Dummy.B) shouldBe "B"
    }

  }

  describe(".queryBinder") {

    val subject = queryBinder(Dummy)

    it("should create an enumeration binder that can bind strings corresponding to enum strings regardless of case") {
      subject.bind("hello", Map("hello" -> Seq("A"))).value.right.value should be(Dummy.A)
    }

    it("should create an enumeration binder that cannot bind strings not found in the enumeration") {
      subject.bind("hello", Map("hello" -> Seq("Z"))).value should be('left)
      subject.bind("hello", Map("helloz" -> Seq("A"))) shouldBe None
    }

    it("should create an enumeration binder that can unbind values") {
      subject.unbind("hello", Dummy.A) should be("hello=A")
      subject.unbind("hello", Dummy.B) should be("hello=B")
    }

  }

}
