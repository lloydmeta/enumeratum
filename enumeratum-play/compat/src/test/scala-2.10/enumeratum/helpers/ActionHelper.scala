package enumeratum.helpers

import play.api.mvc.{Action, AnyContent, AnyContentAsEmpty, BodyParsers, Result}

object ActionHelper {

  def apply(block: => Result): Action[AnyContent] =
    Action(block)
}
