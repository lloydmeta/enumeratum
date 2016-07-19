package enumeratum

import play.api.mvc.QueryStringBindable

trait PlayUppercaseQueryBindableEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val queryBindable: QueryStringBindable[A] = UrlBinders.queryBinderUppercaseOnly(self)
}
