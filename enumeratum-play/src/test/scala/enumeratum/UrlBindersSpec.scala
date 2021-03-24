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

    it("should not create an enumeration binder with an invalid enum value") {
      subject.bind("hello", "Z") shouldBe Left(s"Unknown value supplied for $Dummy 'Z'")
    }
  }

  describe(".pathBinder case insensitive") {

    val subject = pathBinder(Dummy, true)

    it(
      "should create an enumeration binder that can bind strings corresponding to enum strings, disregarding case"
    ) {
      subject.bind("hello", "A").right.value shouldBe Dummy.A
      subject.bind("hello", "a").right.value shouldBe Dummy.A
      subject.bind("hello", "B").right.value shouldBe Dummy.B
      subject.bind("hello", "b").right.value shouldBe Dummy.B
    }

    it("should create an enumeration binder that cannot bind strings not found in the enumeration") {
      subject.bind("hello", "Z").isLeft shouldBe true
    }

    it("should create an enumeration binder that can unbind values") {
      subject.unbind("hello", Dummy.A) shouldBe "A"
      subject.unbind("hello", Dummy.B) shouldBe "B"
    }

  }

  describe(".pathBinder lower case") {

    val subject = pathBinderLowercaseOnly(Dummy)

    it("should create an enumeration binder that can bind strings corresponding to enum strings") {
      subject.bind("hello", "a").right.value shouldBe Dummy.A
      subject.bind("hello", "b").right.value shouldBe Dummy.B
    }

    it("should create an enumeration binder that cannot bind strings not found in the enumeration") {
      subject.bind("hello", "Z").isLeft shouldBe true
    }

    it(
      "should create an enumeration binder that cannot bind strings that aren't lower case but are mixed case"
    ) {
      subject.bind("hello", "A").isLeft shouldBe true
    }

    it("should create an enumeration binder that can unbind values") {
      subject.unbind("hello", Dummy.A) shouldBe "a"
      subject.unbind("hello", Dummy.B) shouldBe "b"
    }

    it("should not create an enumeration binder with an invalid enum value") {
      subject.bind("hello", "Z") shouldBe Left(s"Unknown value supplied for $Dummy 'Z'")
    }
  }

  describe(".pathBinder upper case") {

    val subject = pathBinderUppercaseOnly(Dummy)

    it("should create an enumeration binder that can bind strings corresponding to enum strings") {
      subject.bind("hello", "A").right.value shouldBe Dummy.A
      subject.bind("hello", "B").right.value shouldBe Dummy.B
    }

    it("should create an enumeration binder that cannot bind strings not found in the enumeration") {
      subject.bind("hello", "Z").isLeft shouldBe true
    }

    it(
      "should create an enumeration binder that cannot bind strings that aren't upper case but are mixed case"
    ) {
      subject.bind("hello", "a").isLeft shouldBe true
    }

    it("should create an enumeration binder that can unbind values") {
      subject.unbind("hello", Dummy.A) shouldBe "A"
      subject.unbind("hello", Dummy.B) shouldBe "B"
    }

    it("should not create an enumeration binder with an invalid enum value") {
      subject.bind("hello", "Z") shouldBe Left(s"Unknown value supplied for $Dummy 'Z'")
    }
  }

  describe(".queryBinder") {

    val subject = queryBinder(Dummy)

    it(
      "should create an enumeration binder that can bind strings corresponding to enum strings regardless of case"
    ) {
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

    it("should have the enum on the Left message") {
      subject.bind("hello", Map("hello" -> Seq("Z"))) shouldBe Some(
        Left(s"Cannot parse parameter hello as an Enum: $Dummy"))
    }
  }

  describe(".queryBinder case insensitive") {

    val subject = queryBinder(Dummy, true)

    it(
      "should create an enumeration binder that can bind strings corresponding to enum strings regardless of case, disregarding case"
    ) {
      subject.bind("hello", Map("hello" -> Seq("A"))).value.right.value should be(Dummy.A)
      subject.bind("hello", Map("hello" -> Seq("a"))).value.right.value should be(Dummy.A)
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

  describe(".queryBinder lower case") {

    val subject = queryBinderLowercaseOnly(Dummy)

    it(
      "should create an enumeration binder that can bind strings corresponding to enum strings regardless of case"
    ) {
      subject.bind("hello", Map("hello" -> Seq("a"))).value.right.value should be(Dummy.A)
    }

    it("should create an enumeration binder that cannot bind strings not found in the enumeration") {
      subject.bind("hello", Map("hello" -> Seq("Z"))).value should be('left)
      subject.bind("hello", Map("helloz" -> Seq("a"))) shouldBe None
    }

    it(
      "should create an enumeration binder that cannot bind strings that aren't lower case but are mixed case"
    ) {
      subject.bind("hello", Map("hello" -> Seq("A"))).value should be('left)
    }

    it("should create an enumeration binder that can unbind values") {
      subject.unbind("hello", Dummy.A) should be("hello=a")
      subject.unbind("hello", Dummy.B) should be("hello=b")
    }

    it("should not create an enumeration binder with an invalid enum value") {
      subject.bind("hello", Map("hello" -> Seq("Z"))).value shouldBe Left(
        s"Cannot parse parameter hello as an Enum: $Dummy")
    }
  }

  describe(".queryBinder upper case") {

    val subject = queryBinderUppercaseOnly(Dummy)

    it(
      "should create an enumeration binder that can bind strings corresponding to enum strings regardless of case"
    ) {
      subject.bind("hello", Map("hello" -> Seq("A"))).value.right.value should be(Dummy.A)
    }

    it("should create an enumeration binder that cannot bind strings not found in the enumeration") {
      subject.bind("hello", Map("hello" -> Seq("Z"))).value should be('left)
      subject.bind("hello", Map("helloz" -> Seq("A"))) shouldBe None
    }

    it(
      "should create an enumeration binder that cannot bind strings that aren't upper case but are mixed case"
    ) {
      subject.bind("hello", Map("hello" -> Seq("a"))).value should be('left)
    }

    it("should create an enumeration binder that can unbind values") {
      subject.unbind("hello", Dummy.A) should be("hello=A")
      subject.unbind("hello", Dummy.B) should be("hello=B")
    }

    it("should not create an enumeration binder with an invalid enum value") {
      subject.bind("hello", Map("hello" -> Seq("Z"))).value shouldBe Left(
        s"Cannot parse parameter hello as an Enum: $Dummy")
    }
  }

}
