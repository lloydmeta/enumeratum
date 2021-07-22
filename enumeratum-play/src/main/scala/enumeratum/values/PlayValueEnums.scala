package enumeratum.values

/** Created by Lloyd on 4/13/16.
  *
  * Copyright 2016
  */
/** An IntEnum that has a lot of the Play-related implicits built-in so you can avoid boilerplate.
  *
  * Things included are:
  *
  *   - implicit PathBindable (for binding from request path)
  *   - implicit QueryStringBindable (for binding from query strings)
  *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
  *   - implicit Json format
  *
  * Example:
  *
  * {{{
  * scala> import enumeratum.values._
  * scala> import play.api.data.Form
  * scala> import play.api.libs.json._
  *
  * scala> sealed abstract class Greeting(val value:Int) extends IntEnumEntry
  *
  * scala> object Greeting extends IntPlayEnum[Greeting] {
  *     |   val values = findValues
  *     |   case object Hello   extends Greeting(1)
  *     |   case object GoodBye extends Greeting(2)
  *     |   case object Hi      extends Greeting(3)
  *     |   case object Bye     extends Greeting(4)
  *     | }
  *
  * scala> val form = Form("greeting" -> Greeting.formField)
  * scala> form.bind(Map("greeting" -> "1")).value
  * res0: Option[Greeting] = Some(Hello)
  * }}}
  *
  * scala> Json.toJson(Greeting.Hello) res1: JsValue = 1
  */
trait IntPlayEnum[EnumEntry <: IntEnumEntry]
    extends IntEnum[EnumEntry]
    with IntPlayPathBindableValueEnum[EnumEntry]
    with IntPlayQueryBindableValueEnum[EnumEntry]
    with IntPlayFormValueEnum[EnumEntry]
    with IntPlayJsonValueEnum[EnumEntry]

/** A LongEnum that has a lot of the Play-related implicits built-in so you can avoid boilerplate.
  *
  * Things included are:
  *
  *   - implicit PathBindable (for binding from request path)
  *   - implicit QueryStringBindable (for binding from query strings)
  *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
  *   - implicit Json format
  *
  * See [[IntPlayEnum]] for example usage.
  */
trait LongPlayEnum[EnumEntry <: LongEnumEntry]
    extends LongEnum[EnumEntry]
    with LongPlayPathBindableValueEnum[EnumEntry]
    with LongPlayQueryBindableValueEnum[EnumEntry]
    with LongPlayFormValueEnum[EnumEntry]
    with LongPlayJsonValueEnum[EnumEntry]

/** A ShortEnum that has a lot of the Play-related implicits built-in so you can avoid boilerplate.
  *
  * Things included are:
  *
  *   - implicit PathBindable (for binding from request path)
  *   - implicit QueryStringBindable (for binding from query strings)
  *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
  *   - implicit Json format
  *
  * See [[IntPlayEnum]] for example usage.
  */
trait ShortPlayEnum[EnumEntry <: ShortEnumEntry]
    extends ShortEnum[EnumEntry]
    with ShortPlayPathBindableValueEnum[EnumEntry]
    with ShortPlayQueryBindableValueEnum[EnumEntry]
    with ShortPlayFormValueEnum[EnumEntry]
    with ShortPlayJsonValueEnum[EnumEntry]

/** A StringEnum that has a lot of the Play-related implicits built-in so you can avoid boilerplate.
  *
  * Things included are:
  *
  *   - implicit PathBindable (for binding from request path)
  *   - implicit QueryStringBindable (for binding from query strings)
  *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
  *   - implicit Json format
  *
  * See [[IntPlayEnum]] for example usage.
  */
trait StringPlayEnum[EnumEntry <: StringEnumEntry]
    extends StringEnum[EnumEntry]
    with StringPlayPathBindableValueEnum[EnumEntry]
    with StringPlayQueryBindableValueEnum[EnumEntry]
    with StringPlayFormValueEnum[EnumEntry]
    with StringPlayJsonValueEnum[EnumEntry]

/** A ByteEnum that has a lot of the Play-related implicits built-in so you can avoid boilerplate.
  *
  * Things included are:
  *
  *   - implicit PathBindable (for binding from request path)
  *   - implicit QueryByteBindable (for binding from query strings)
  *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
  *   - implicit Json format
  *
  * See [[IntPlayEnum]] for example usage.
  */
trait BytePlayEnum[EnumEntry <: ByteEnumEntry]
    extends ByteEnum[EnumEntry]
    with BytePlayPathBindableValueEnum[EnumEntry]
    with BytePlayQueryBindableValueEnum[EnumEntry]
    with BytePlayFormValueEnum[EnumEntry]
    with BytePlayJsonValueEnum[EnumEntry]

/** A CharEnum that has a lot of the Play-related implicits built-in so you can avoid boilerplate.
  *
  * Things included are:
  *
  *   - implicit PathBindable (for binding from request path)
  *   - implicit QueryCharBindable (for binding from query strings)
  *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
  *   - implicit Json format
  *
  * See [[IntPlayEnum]] for example usage.
  */
trait CharPlayEnum[EnumEntry <: CharEnumEntry]
    extends CharEnum[EnumEntry]
    with CharPlayPathBindableValueEnum[EnumEntry]
    with CharPlayQueryBindableValueEnum[EnumEntry]
    with CharPlayFormValueEnum[EnumEntry]
    with CharPlayJsonValueEnum[EnumEntry]
