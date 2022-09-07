package enumeratum.values

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import play.api.data.Form
import play.api.http.HttpVerbs
import org.scalatest.OptionValues._
import org.scalatest.EitherValues._
import play.api.libs.json.{Format, JsNumber, JsValue}
import play.api.test.FakeRequest
import enumeratum.helpers.ActionHelper

/** Created by Lloyd on 4/13/16.
  *
  * Copyright 2016
  */
trait PlayValueEnumHelpers extends EnumJsonFormatHelpers { this: AnyFunSpec with Matchers =>

  def testNumericPlayEnum[EntryType <: ValueEnumEntry[
    ValueType
  ], ValueType <: AnyVal: Numeric: Format](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType]
        with PlayFormValueEnum[ValueType, EntryType]
        with PlayPathBindableValueEnum[ValueType, EntryType]
        with PlayQueryBindableValueEnum[ValueType, EntryType]
        with PlayJsonValueEnum[ValueType, EntryType]
  ) = {
    val numeric = implicitly[Numeric[ValueType]]
    testPlayEnum(
      enumKind,
      myEnum,
      { (i: ValueType) =>
        JsNumber(numeric.toInt(i))
      }
    )

  }

  def testPlayEnum[EntryType <: ValueEnumEntry[ValueType], ValueType: Format](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType]
        with PlayFormValueEnum[ValueType, EntryType]
        with PlayPathBindableValueEnum[ValueType, EntryType]
        with PlayQueryBindableValueEnum[ValueType, EntryType]
        with PlayJsonValueEnum[ValueType, EntryType],
      jsWrapper: ValueType => JsValue
  ) = {
    describe(enumKind) {

      describe("Form binding") {

        val subject = Form("hello" -> myEnum.formField)

        it("should bind proper strings into an Enum value") {
          myEnum.values.foreach { entry =>
            subject.bind(Map("hello" -> s"${entry.value}")).value.value shouldBe entry
          }
        }

        it("should fail to bind random strings") {
          subject.bind(Map("hello" -> "AARS143515123E")).value shouldBe None

          subject.bind(Map("hello" -> s"${Int.MaxValue}")).value shouldBe None
        }

        it("should unbind") {
          myEnum.values.foreach { entry =>
            val r = subject.mapping.unbind(entry)
            r shouldBe Map("hello" -> s"${entry.value}")
          }
        }

      }

      describe("URL binding") {

        describe("PathBindable") {

          val subject = myEnum.pathBindable

          it("should bind strings corresponding to enum values") {
            myEnum.values.foreach { entry =>
              subject.bind("hello", s"${entry.value}").right.value shouldBe entry
            }
          }

          it("should not bind strings not found as values in the enumeration") {
            subject.bind("hello", s"s${Int.MaxValue}").isLeft shouldBe true
            subject.bind("hello", "Z").isLeft shouldBe true
          }

          it("should unbind values") {
            myEnum.values.foreach { entry =>
              subject.unbind("hello", entry) shouldBe entry.value.toString
            }
          }

        }

        describe("PathBindableExtractor") {

          val subject = myEnum.fromPath

          it("should extract strings corresponding to enum values") {
            myEnum.values.foreach { entry =>
              subject.unapply(s"${entry.value}") shouldBe Some(entry)
            }
          }

          it("should not extract strings that are not found as valuesin the enumeration") {
            subject.unapply("Z") shouldBe None
            subject.unapply(s"${Int.MaxValue}") shouldBe None
          }

          it("should allow me to build an SIRD router") {
            import play.api.routing.sird._
            import play.api.routing._
            import play.api.mvc._
            myEnum.values.foreach { entry =>
              val router = Router.from { case GET(p"/${myEnum.fromPath(greeting)}") =>
                ActionHelper {
                  Results.Ok(s"$greeting")
                }
              }
              router.routes.isDefinedAt(
                FakeRequest(HttpVerbs.GET, s"/${entry.value}")
              ) shouldBe true
              router.routes.isDefinedAt(
                FakeRequest(HttpVerbs.GET, s"/${Int.MaxValue}")
              ) shouldBe false
            }
          }

        }

        describe("QueryStringBindable") {

          val subject = myEnum.queryBindable

          it("should bind strings corresponding to enum values regardless of case") {
            myEnum.values.foreach { entry =>
              subject
                .bind("hello", Map("hello" -> Seq(s"${entry.value}")))
                .value
                .right
                .value shouldBe entry
            }
          }

          it("should not bind strings not found as values in the enumeration") {
            subject.bind("hello", Map("hello" -> Seq("Z"))).value shouldBe Symbol("left")
            subject.bind("hello", Map("hello" -> Seq(s"${Int.MaxValue}"))).value shouldBe Symbol(
              "left"
            )
            subject.bind("hello", Map("helloz" -> Seq("1"))) shouldBe None
          }

          it("should unbind values") {
            myEnum.values.foreach { entry =>
              subject.unbind("hello", entry) shouldBe s"hello=${entry.value}"
            }
          }

        }

        describe("JSON formats") {
          testFormats(enumKind, myEnum, jsWrapper, Some(myEnum.format))
        }

      }
    }

  }

}
