package enumeratum

import play.api.mvc.PathBindable
import play.api.routing.sird.PathBindableExtractor

trait PlayPathBindableEnum[A <: EnumEntry] { self: Enum[A] =>

  /**
    * Implicit path binder for Play's default router
    */
  implicit val pathBindable: PathBindable[A] = UrlBinders.pathBinder(self)

  /**
    * Binder for [[play.api.routing.sird]] router
    *
    * Example:
    *
    * {{{
    *  import play.api.routing.sird._
    *  import play.api.routing._
    *  import play.api.mvc._
    *
    *  Router.from {
    *    case GET(p"/hello/${Greeting.fromPath(greeting)}") => Action {
    *      Results.Ok(s"$greeting")
    *    }
    *  }
    * }}}
    */
  lazy val fromPath = new PathBindableExtractor[A]
}