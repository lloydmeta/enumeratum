package enumeratum.helpers

import play.api.mvc.{Action, ActionBuilder, AnyContent, AnyContentAsEmpty, BodyParsers, Result}

object ActionHelper {

  import scala.concurrent.ExecutionContext.Implicits.global

  def apply(block: => Result): Action[AnyContent] =
    new ActionBuilder.IgnoringBody().apply(block)
}
