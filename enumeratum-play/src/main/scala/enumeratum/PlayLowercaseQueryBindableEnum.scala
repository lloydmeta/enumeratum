package enumeratum

import play.api.mvc.QueryStringBindable

trait PlayLowercaseQueryBindableEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val queryBindable: QueryStringBindable[A] =
    UrlBinders.queryBinderLowercaseOnly(self)
}
