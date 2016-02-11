package enumeratum

import play.api.mvc.QueryStringBindable

trait PlayQueryBindableEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val queryBindable: QueryStringBindable[A] = UrlBinders.queryBinder(self)
}
