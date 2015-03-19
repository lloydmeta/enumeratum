package enumeratum

import play.api.mvc.PathBindable

trait PlayPathBindable[A] { self: Enum[A] =>
  implicit val pathBindable: PathBindable[A] = UrlBinders.pathBinder(this)
}