package enumeratum

import play.api.mvc.QueryStringBindable

trait PlayQueryBindableEnum[A] { self: Enum[A] =>
  implicit val queryBindable: QueryStringBindable[A] = UrlBinders.queryBinder(this)
}
