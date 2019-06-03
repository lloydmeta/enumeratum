package enumeratum

import org.scalatest.{FunSpec, Matchers}
import play.api.data.{Form, Mapping}
import play.api.http.HttpVerbs
import play.api.libs.json.{Format, JsNumber, JsString, JsValue, Json => PlayJson}
import org.scalatest.OptionValues._
import org.scalatest.EitherValues._
import play.api.mvc.{PathBindable, QueryStringBindable}
import play.api.routing.sird.PathBindableExtractor
import play.api.test.FakeRequest
import enumeratum.helpers.ActionHelper

class PlayEnumSpec extends FunSpec with Matchers {

  testScenarios(
    descriptor = "ordinary operation (no tarnsforms)",
    enum = PlayDummyNormal,
    validTransforms =
      Map("A" -> PlayDummyNormal.A, "B" -> PlayDummyNormal.B, "c" -> PlayDummyNormal.c),
    expectedFailures = Seq("1.234"),
    formMapping = PlayDummyNormal.formField,
    pathBindable = PlayDummyNormal.pathBindable,
    pathBindableExtractor = PlayDummyNormal.fromPath,
    queryStringBindable = PlayDummyNormal.queryBindable
  )

  testScenarios(
    descriptor = "lower case transformed",
    enum = PlayDummyLowerOnly,
    validTransforms =
      Map("a" -> PlayDummyLowerOnly.A, "b" -> PlayDummyLowerOnly.B, "c" -> PlayDummyLowerOnly.c),
    expectedFailures = Seq("C"),
    formMapping = PlayDummyLowerOnly.formField,
    pathBindable = PlayDummyLowerOnly.pathBindable,
    pathBindableExtractor = PlayDummyLowerOnly.fromPath,
    queryStringBindable = PlayDummyLowerOnly.queryBindable
  )

  testScenarios(
    descriptor = "upper case transformed",
    enum = PlayDummyUpperOnly,
    validTransforms =
      Map("A" -> PlayDummyUpperOnly.A, "B" -> PlayDummyUpperOnly.B, "C" -> PlayDummyUpperOnly.c),
    expectedFailures = Seq("c"),
    formMapping = PlayDummyUpperOnly.formField,
    pathBindable = PlayDummyUpperOnly.pathBindable,
    pathBindableExtractor = PlayDummyUpperOnly.fromPath,
    queryStringBindable = PlayDummyUpperOnly.queryBindable
  )

  private def testScenarios[A <: EnumEntry: Format](
      descriptor: String,
      enum: Enum[A],
      validTransforms: Map[String, A],
      expectedFailures: Seq[String],
      formMapping: Mapping[A],
      pathBindable: PathBindable[A],
      pathBindableExtractor: PathBindableExtractor[A],
      queryStringBindable: QueryStringBindable[A]
  ): Unit = describe(descriptor) {

    testJson()
    testFormBinding()
    testUrlBinding()

    def testJson(): Unit = {

      val failures: Seq[JsValue] = expectedFailures.map(JsString) ++ Seq(
        JsString("AVADSGDSAFA"),
        JsNumber(Int.MaxValue)
      )

      describe("JSON serdes") {

        describe("deserialisation") {

          it("should work with valid values") {
            validTransforms.foreach {
              case (k, v) =>
                JsString(k).asOpt[A].value shouldBe v
            }
          }

          it("should fail with invalid values") {
            failures.foreach { v =>
              v.asOpt[A] shouldBe None
            }
          }
        }

        describe("serialisation") {

          it("should serialise values to JsString") {
            validTransforms.foreach {
              case (k, v) =>
                PlayJson.toJson(v) shouldBe JsString(k)
            }
          }

        }

      }
    }

    def testFormBinding(): Unit = {

      val subject        = Form("hello" -> formMapping)
      val expectedErrors = expectedFailures ++ Seq(Int.MaxValue.toString, "12asdf13!")

      describe("Form binding") {
        it("should bind proper strings into an Enum value") {
          validTransforms.foreach {
            case (k, v) =>
              val r = subject.bind(Map("hello" -> k))
              r.value.value shouldBe v
          }
        }

        it("should fail to bind random strings") {
          expectedErrors.foreach { s =>
            val r = subject.bind(Map("hello" -> s))
            r.value shouldBe None
          }
        }
      }
    }

    def testUrlBinding(): Unit = {

      val expectedErrors = expectedFailures ++ Seq("1", "abc123", "Z", "F")

      describe("URL Binding") {

        describe("PathBindable") {
          it("should bind strings corresponding to enum strings") {
            validTransforms.foreach {
              case (k, v) =>
                pathBindable.bind("hello", k).right.value shouldBe v
            }
          }

          it("should not bind strings not found in the enumeration") {
            expectedErrors.foreach { v =>
              pathBindable.bind("hello", v).isLeft shouldBe true
            }
          }

          it("should unbind values") {
            validTransforms.foreach {
              case (k, v) =>
                pathBindable.unbind("hello", v) shouldBe k
            }
          }
        }

        describe("PathBindableExtractor") {

          it("should extract strings corresponding to enum strings") {
            validTransforms.foreach {
              case (k, v) =>
                pathBindableExtractor.unapply(k) shouldBe Some(v)
            }
          }

          it("should not extract strings that are not in the enumeration") {
            expectedErrors.foreach { v =>
              pathBindableExtractor.unapply(v) shouldBe None
            }
          }

          it("should allow me to build an SIRD router") {
            import play.api.routing.sird._
            import play.api.routing._
            import play.api.mvc._
            val router = Router.from {
              case GET(p"/${pathBindableExtractor(greeting)}") =>
                ActionHelper {
                  Results.Ok(s"$greeting")
                }
            }
            validTransforms.foreach {
              case (k, v) =>
                router.routes.isDefinedAt(FakeRequest(HttpVerbs.GET, s"/$k")) shouldBe true
            }
            expectedErrors.foreach { v =>
              router.routes.isDefinedAt(FakeRequest(HttpVerbs.GET, s"/$v")) shouldBe false
            }
          }

        }

        describe("QueryStringBindable") {

          it("should bind strings corresponding to enum strings") {
            validTransforms.foreach {
              case (k, v) =>
                queryStringBindable
                  .bind("hello", Map("hello" -> Seq(k)))
                  .value
                  .right
                  .value should be(v)
            }
          }

          it("should not bind strings not found in the enumeration") {
            expectedErrors.foreach { v =>
              queryStringBindable.bind("hello", Map("hello" -> Seq(v))).value shouldBe 'left
              queryStringBindable.bind("hello", Map("helloz" -> Seq(v))) shouldBe None
            }
          }

          it("should unbind values") {
            validTransforms.foreach {
              case (k, v) =>
                queryStringBindable.unbind("hello", v) shouldBe s"hello=$k"
            }
          }

        }

      }
    }

  }

}
