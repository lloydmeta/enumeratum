package enumeratum

import play.api.data.Mapping

trait PlayUppercaseFormFieldEnum[A <: EnumEntry] { self: Enum[A] =>

  /**
   * Form field for this enum
   */
  val formField: Mapping[A] = Forms.enumUppercaseOnly(self)
}
