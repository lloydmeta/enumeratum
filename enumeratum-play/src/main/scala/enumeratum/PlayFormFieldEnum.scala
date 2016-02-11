package enumeratum

import play.api.data.Mapping

trait PlayFormFieldEnum[A <: EnumEntry] { self: Enum[A] =>
  val formField: Mapping[A] = Forms.enum(self)
}
