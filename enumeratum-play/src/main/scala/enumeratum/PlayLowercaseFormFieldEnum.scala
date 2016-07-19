package enumeratum

import play.api.data.Mapping

trait PlayLowercaseFormFieldEnum[A <: EnumEntry] { self: Enum[A] =>

  /**
   * Form field for this enum
   */
  val formField: Mapping[A] = Forms.enumLowerCaseOnly(self)
}

