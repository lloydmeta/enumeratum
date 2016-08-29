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
 * Things included are:
 *
 *   - implicit PathBindable (for binding from request path)
 *   - implicit QueryStringBindable (for binding from query strings)
 *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
 *   - implicit Json format
 *
 */
trait IntPlayEnum[EnumEntry <: IntEnumEntry] extends IntEnum[EnumEntry]
  with IntPlayPathBindableValueEnum[EnumEntry]
  with IntPlayQueryBindableValueEnum[EnumEntry]
  with IntPlayFormValueEnum[EnumEntry]
  with IntPlayJsonValueEnum[EnumEntry]

/**
 * A LongEnum that has a lot of the Play-related implicits built-in so you can avoid
 * boilerplate.
 *
 * Things included are:
 *
 *   - implicit PathBindable (for binding from request path)
 *   - implicit QueryStringBindable (for binding from query strings)
 *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
 *   - implicit Json format
 *
 */
trait LongPlayEnum[EnumEntry <: LongEnumEntry] extends LongEnum[EnumEntry]
  with LongPlayPathBindableValueEnum[EnumEntry]
  with LongPlayQueryBindableValueEnum[EnumEntry]
  with LongPlayFormValueEnum[EnumEntry]
  with LongPlayJsonValueEnum[EnumEntry]

/**
 * A ShortEnum that has a lot of the Play-related implicits built-in so you can avoid
 * boilerplate.
 *
 * Things included are:
 *
 *   - implicit PathBindable (for binding from request path)
 *   - implicit QueryStringBindable (for binding from query strings)
 *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
 *   - implicit Json format
 *
 */
trait ShortPlayEnum[EnumEntry <: ShortEnumEntry] extends ShortEnum[EnumEntry]
  with ShortPlayPathBindableValueEnum[EnumEntry]
  with ShortPlayQueryBindableValueEnum[EnumEntry]
  with ShortPlayFormValueEnum[EnumEntry]
  with ShortPlayJsonValueEnum[EnumEntry]

/**
 * A StringEnum that has a lot of the Play-related implicits built-in so you can avoid
 * boilerplate.
 *
 * Things included are:
 *
 *   - implicit PathBindable (for binding from request path)
 *   - implicit QueryStringBindable (for binding from query strings)
 *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
 *   - implicit Json format
 *
 */
trait StringPlayEnum[EnumEntry <: StringEnumEntry] extends StringEnum[EnumEntry]
  with StringPlayPathBindableValueEnum[EnumEntry]
  with StringPlayQueryBindableValueEnum[EnumEntry]
  with StringPlayFormValueEnum[EnumEntry]
  with StringPlayJsonValueEnum[EnumEntry]