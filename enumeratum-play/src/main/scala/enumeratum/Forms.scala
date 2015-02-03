package enumeratum

import play.api.data.format.Formatter
import play.api.data.{ FormError, Forms => PlayForms, Mapping }

/**
 * Created by Lloyd on 2/3/15.
 */
object Forms {

  /**
   * Returns an [[Enum]] mapping
   */
  def maps[A](enum: Enum[A]): Mapping[A] = PlayForms.of(format(enum))

  /**
   * Returns a Formatter for [[Enum]]
   */
  def format[A](enum: Enum[A]): Formatter[A] = new Formatter[A] {
    def bind(key: String, data: Map[String, String]) = {
      play.api.data.format.Formats.stringFormat.bind(key, data).right.flatMap { s =>
        scala.util.control.Exception.allCatch[A]
          .either(enum.withName(s))
          .left.map(e => Seq(FormError(key, "error.enum", Nil)))
      }
    }
    def unbind(key: String, value: A) = Map(key -> value.toString)
  }

}
