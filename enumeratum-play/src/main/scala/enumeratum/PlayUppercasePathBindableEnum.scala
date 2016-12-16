package enumeratum

import play.api.mvc.PathBindable
import play.api.routing.sird.PathBindableExtractor

trait PlayUppercasePathBindableEnum[A <: EnumEntry] { self: Enum[A] =>

  /**
    * Implicit path binder for Play's default router
    */
  implicit val pathBindable: PathBindable[A] =
    UrlBinders.pathBinderUppercaseOnly(self)

  /**
    * Binder for [[play.api.routing.sird]] router
    */
  lazy val fromPath = new PathBindableExtractor[A]
}
