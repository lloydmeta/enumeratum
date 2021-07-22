package enumeratum

/** An Enum that has a lot of the Play-related implicits built-in so you can avoid boilerplate.
  *
  * Note, the binders created here are case-sensitive.
  *
  * Things included are:
  *
  *   - implicit JSON format
  *   - implicit PathBindable (for binding from request path)
  *   - implicit QueryStringBindable (for binding from query strings)
  *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
  *
  * Example:
  *
  * {{{
  * scala> import enumeratum._
  * scala> import play.api.data.Form
  * scala> import play.api.libs.json._
  *
  * scala> sealed trait Greeting extends EnumEntry
  *
  * scala> object Greeting extends PlayEnum[Greeting] {
  *     |   val values = findValues
  *     |   case object Hello   extends Greeting
  *     |   case object GoodBye extends Greeting
  *     |   case object Hi      extends Greeting
  *     |   case object Bye     extends Greeting
  *     | }
  *
  * scala> val form = Form("greeting" -> Greeting.formField)
  * scala> form.bind(Map("greeting" -> "Hello")).value
  * res0: Option[Greeting] = Some(Hello)
  * }}}
  *
  * scala> Json.toJson(Greeting.Hello) res1: JsValue = "Hello"
  */
trait PlayEnum[A <: EnumEntry]
    extends Enum[A]
    with PlayJsonEnum[A]
    with PlayPathBindableEnum[A]
    with PlayQueryBindableEnum[A]
    with PlayFormFieldEnum[A]
