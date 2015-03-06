package enumeratum

import play.api.data.format.Formatter
import play.api.data.{ FormError, Forms => PlayForms, Mapping }

/**
 * Created by Lloyd on 2/3/15.
 */
object Forms {

  /**
   * Returns an [[Enum]] mapping
   *
   * For example:
   * {{{
   *   Form("status" -> maps(Status))
   * }}}
   */
  def enum[A](enum: Enum[A]): Mapping[A] = PlayForms.of(format(enum))

  /**
   * Returns a Formatter for [[Enum]]
   */
  private[enumeratum] def format[A](enum: Enum[A]): Formatter[A] = new Formatter[A] {
    def bind(key: String, data: Map[String, String]) = {
      play.api.data.format.Formats.stringFormat.bind(key, data).right.flatMap { s =>
        enum.withNameOption(s) match {
          case Some(obj) => Right(obj)
          case None => Left(Seq(FormError(key, "error.enum", Nil)))
        }
      }
    }
    def unbind(key: String, value: A) = Map(key -> value.toString)
  }

}
