package enumeratum.values

import org.scalatest.{ FunSpec, Matchers }
import play.api.data.Form
import org.scalatest.OptionValues._
import org.scalatest.EitherValues._
import play.api.http.HttpVerbs
import play.api.mvc.{ Headers, RequestHeader }

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */
class PlayValueEnumSpec extends FunSpec with Matchers {

  describe("Form binding") {

    val subject = Form("hello" -> PlayContentType.formField)

    it("should bind proper strings into an Enum value") {
      val r1 = subject.bind(Map("hello" -> "1"))
      val r2 = subject.bind(Map("hello" -> "2"))
      r1.value.value shouldBe PlayContentType.Text
      r2.value.value shouldBe PlayContentType.Image
    }

    it("should fail to bind random strings") {
      val r = subject.bind(Map("hello" -> "AARSE"))
      r.value shouldBe None
    }

  }

  describe("URL binding") {

    describe("PathBindable") {

      val subject = PlayContentType.pathBindable

      it("should bind strings corresponding to enum values") {
        subject.bind("hello", "1").right.value shouldBe PlayContentType.Text
      }

      it("should not bind strings not found as values in the enumeration") {
        subject.bind("hello", "10").isLeft shouldBe true
        subject.bind("hello", "Z").isLeft shouldBe true
      }

      it("should unbind values") {
        subject.unbind("hello", PlayContentType.Text) shouldBe "1"
        subject.unbind("hello", PlayContentType.Image) shouldBe "2"
      }

    }

    describe("PathBindableExtractor") {

      val subject = PlayContentType.fromPath

      it("should extract strings corresponding to enum values") {
        subject.unapply("1") shouldBe Some(PlayContentType.Text)
        subject.unapply("2") shouldBe Some(PlayContentType.Image)
        subject.unapply("3") shouldBe Some(PlayContentType.Video)
      }

      it("should not extract strings that are not found as valuesin the enumeration") {
        subject.unapply("Z") shouldBe None
        subject.unapply("10") shouldBe None
      }

      it("should allow me to build an SIRD router") {
        import play.api.routing.sird._
        import play.api.routing._
        import play.api.mvc._
        val router = Router.from {
          case GET(p"/${ PlayContentType.fromPath(greeting) }") => Action {
            Results.Ok(s"$greeting")
          }
        }
        router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, "/1")) shouldBe true
        router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, "/10")) shouldBe false
      }

    }

    describe("QueryStringBindable") {

      val subject = PlayContentType.queryBindable

      it("should bind strings corresponding to enum values regardless of case") {
        subject.bind("hello", Map("hello" -> Seq("1"))).value.right.value should be(PlayContentType.Text)
      }

      it("should not bind strings not found as values in the enumeration") {
        subject.bind("hello", Map("hello" -> Seq("Z"))).value should be('left)
        subject.bind("hello", Map("hello" -> Seq("10"))).value should be('left)
        subject.bind("hello", Map("helloz" -> Seq("1"))) shouldBe None
      }

      it("should unbind values") {
        subject.unbind("hello", PlayContentType.Text) should be("hello=1")
        subject.unbind("hello", PlayContentType.Audio) should be("hello=4")
      }

    }

  }

  private def reqHeaderAt(theMethod: String, theUri: String) =
    new RequestHeader {
      def secure: Boolean = ???

      def uri: String = theUri

      def remoteAddress: String = ???

      def queryString: Map[String, Seq[String]] = ???

      def method: String = theMethod

      def headers: Headers = ???

      def path: String = uri

      def version: String = ???

      def tags: Map[String, String] = ???

      def id: Long = ???
    }

}

sealed abstract class PlayContentType(val value: Long, name: String) extends LongEnumEntry

case object PlayContentType
    extends PlayLongEnum[PlayContentType] {

  case object Text extends PlayContentType(value = 1L, name = "text")
  case object Image extends PlayContentType(value = 2L, name = "image")
  case object Video extends PlayContentType(value = 3L, name = "video")
  case object Audio extends PlayContentType(value = 4L, name = "audio")

  val values = findValues

}