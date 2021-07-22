package enumeratum

import play.api.data.Mapping

trait PlayFormFieldEnum[A <: EnumEntry] { self: Enum[A] =>

  /** Form field for this enum
    */
  val formField: Mapping[A] = Forms.enum(self)
}
