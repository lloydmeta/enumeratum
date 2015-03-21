package enumeratum

/**
 * An Enum that has a lot of the Play-related implicits built-in so you can avoid
 * boilerplate.
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
 */
trait PlayEnum[A] extends Enum[A]
  with PlayJsonEnum[A]
  with PlayPathBindableEnum[A]
  with PlayQueryBindableEnum[A]
  with PlayFormFieldEnum[A]