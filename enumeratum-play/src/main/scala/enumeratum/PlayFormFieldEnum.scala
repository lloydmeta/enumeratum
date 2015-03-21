package enumeratum

import play.api.data.Mapping

trait PlayFormFieldEnum[A] { self: Enum[A] =>
  val formField: Mapping[A] = Forms.enum(this)
}
