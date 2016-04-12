package enumeratum.values

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */

/**
 * An IntEnum that has a lot of the Play-related implicits built-in so you can avoid
 * boilerplate.
 *
 * Note, the binders created here are case-sensitive.
 *
 * Things included are:
 *
 *   - implicit PathBindable (for binding from request path)
 *   - implicit QueryStringBindable (for binding from query strings)
 *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
 *
 */
trait PlayIntEnum[EnumEntry <: IntEnumEntry] extends IntEnum[EnumEntry]
  with PlayPathBindableIntValueEnum[EnumEntry]
  with PlayQueryBindableIntValueEnum[EnumEntry]
  with PlayFormIntValueEnum[EnumEntry]

/**
 * An LongEnum that has a lot of the Play-related implicits built-in so you can avoid
 * boilerplate.
 *
 * Note, the binders created here are case-sensitive.
 *
 * Things included are:
 *
 *   - implicit PathBindable (for binding from request path)
 *   - implicit QueryStringBindable (for binding from query strings)
 *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
 *
 */
trait PlayLongEnum[EnumEntry <: LongEnumEntry] extends LongEnum[EnumEntry]
  with PlayPathBindableLongValueEnum[EnumEntry]
  with PlayQueryBindableLongValueEnum[EnumEntry]
  with PlayFormLongValueEnum[EnumEntry]

/**
 * An ShortEnum that has a lot of the Play-related implicits built-in so you can avoid
 * boilerplate.
 *
 * Note, the binders created here are case-sensitive.
 *
 * Things included are:
 *
 *   - implicit PathBindable (for binding from request path)
 *   - implicit QueryStringBindable (for binding from query strings)
 *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
 *
 */
trait PlayShortEnum[EnumEntry <: ShortEnumEntry] extends ShortEnum[EnumEntry]
  with PlayPathBindableShortValueEnum[EnumEntry]
  with PlayQueryBindableShortValueEnum[EnumEntry]
  with PlayFormShortValueEnum[EnumEntry]