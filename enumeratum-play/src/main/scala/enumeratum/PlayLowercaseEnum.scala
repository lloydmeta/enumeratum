package enumeratum

/**
 * An Enum that has a lot of the Play-related implicits built-in so you can avoid
 * boilerplate.
 *
 * Note, the binders created here transform to lower case.
 *
 * Things included are:
 *
 *   - implicit JSON format
 *   - implicit PathBindable (for binding from request path)
 *   - implicit QueryStringBindable (for binding from query strings)
 *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
 *
 */
trait PlayLowercaseEnum[A <: EnumEntry] extends Enum[A]
  with PlayLowercaseJsonEnum[A]
  with PlayLowercasePathBindableEnum[A]
  with PlayLowercaseQueryBindableEnum[A]
  with PlayLowercaseFormFieldEnum[A]