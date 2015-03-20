package enumeratum

import play.api.mvc.PathBindable

trait PlayPathBindableEnum[A] { self: Enum[A] =>
  implicit val pathBindable: PathBindable[A] = UrlBinders.pathBinder(this)
}