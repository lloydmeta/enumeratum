package enumeratum

import java.security.cert.X509Certificate

import org.scalatest.{ FunSpec, Matchers }
import play.api.data.Form
import play.api.http.HttpVerbs
import play.api.libs.json.{ JsNumber, JsString, Json => PlayJson }
import org.scalatest.OptionValues._
import org.scalatest.EitherValues._
import play.api.mvc.{ Headers, RequestHeader }
import play.api.routing.Router

class PlayEnumSpec extends FunSpec with Matchers {

  describe("JSON serdes") {

    describe("deserialisation") {

      it("should work with valid values") {
        JsString("A").asOpt[PlayDummy].value shouldBe PlayDummy.A
      }

      it("should fail with invalid values") {
        JsString("D").asOpt[PlayDummy] shouldBe None
        JsNumber(2).asOpt[PlayDummy] shouldBe None
      }
    }

    describe("serialisation") {

      it("should serialise values to JsString") {
        PlayJson.toJson(PlayDummy.A) shouldBe JsString("A")
      }

    }

  }

  describe("Form binding") {

    val subject = Form("hello" -> PlayDummy.formField)

    it("should bind proper strings into an Enum value") {
      val r1 = subject.bind(Map("hello" -> "A"))
      val r2 = subject.bind(Map("hello" -> "B"))
      r1.value.value shouldBe PlayDummy.A
      r2.value.value shouldBe PlayDummy.B
    }

    it("should fail to bind random strings") {
      val r = subject.bind(Map("hello" -> "AARSE"))
      r.value shouldBe None
    }

  }

  describe("URL binding") {

    describe("PathBindable") {

      val subject = PlayDummy.pathBindable

      it("should bind strings corresponding to enum strings") {
        subject.bind("hello", "A").right.value shouldBe PlayDummy.A
      }

      it("should not bind strings not found in the enumeration") {
        subject.bind("hello", "Z").isLeft shouldBe true
      }

      it("should unbind values") {
        subject.unbind("hello", PlayDummy.A) shouldBe "A"
        subject.unbind("hello", PlayDummy.B) shouldBe "B"
      }

    }

    describe("PathBindableExtractor") {

      val subject = PlayDummy.fromPath

      it("should extract strings corresponding to enum strings") {
        subject.unapply("A") shouldBe Some(PlayDummy.A)
        subject.unapply("B") shouldBe Some(PlayDummy.B)
        subject.unapply("C") shouldBe Some(PlayDummy.C)
      }

      it("should not extract strings that are not in the enumeration") {
        subject.unapply("Z") shouldBe None
      }

      it("should allow me to build an SIRD router") {
        import play.api.routing.sird._
        import play.api.routing._
        import play.api.mvc._
        val router = Router.from {
          case GET(p"/${ PlayDummy.fromPath(greeting) }") => Action {
            Results.Ok(s"$greeting")
          }
        }
        router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, "/A")) shouldBe true
        router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, "/F")) shouldBe false
      }

    }

    describe("QueryStringBindable") {

      val subject = PlayDummy.queryBindable

      it("should bind strings corresponding to enum strings regardless of case") {
        subject.bind("hello", Map("hello" -> Seq("A"))).value.right.value should be(PlayDummy.A)
      }

      it("should not bind strings not found in the enumeration") {
        subject.bind("hello", Map("hello" -> Seq("Z"))).value should be('left)
        subject.bind("hello", Map("helloz" -> Seq("A"))) shouldBe None
      }

      it("should unbind values") {
        subject.unbind("hello", PlayDummy.A) should be("hello=A")
        subject.unbind("hello", PlayDummy.B) should be("hello=B")
      }

    }

  }

  private def reqHeaderAt(theMethod: String, theUri: String) =
    new RequestHeader {

      def clientCertificateChain: Option[Seq[X509Certificate]] = ???

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
