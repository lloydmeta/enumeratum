package enumeratum

import play.api.mvc.PathBindable

trait PlayPathBindableEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val pathBindable: PathBindable[A] = UrlBinders.pathBinder(this)
}