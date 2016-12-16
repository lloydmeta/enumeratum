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
    * scala> import play.api.routing.sird._
    * scala> import play.api.routing._
    * scala> import play.api.mvc._
    * scala> import enumeratum._
    *
    * scala> sealed trait Greeting extends EnumEntry
    *
    * scala> object Greeting extends PlayEnum[Greeting] {
    *      |   val values = findValues
    *      |   case object Hello extends Greeting
    *      |   case object GoodBye extends Greeting
    *      |   case object Hi extends Greeting
    *      |   case object Bye extends Greeting
    *      | }
    *
    * scala> val router = Router.from {
    *      |   case GET(p"/hello/${Greeting.fromPath(greeting)}") => Action {
    *      |     Results.Ok(s"$greeting")
    *      |   }
    *      | }
    * scala> router.routes
    * res0: Router.Routes = <function1>
    * }}}
    */
  lazy val fromPath = new PathBindableExtractor[A]
}
