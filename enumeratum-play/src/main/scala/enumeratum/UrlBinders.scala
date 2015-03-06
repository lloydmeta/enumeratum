package enumeratum

import play.api.mvc.PathBindable
import play.api.mvc.QueryStringBindable
import play.api.mvc.QueryStringBindable._

/**
 * Created by Lloyd on 2/3/15.
 */
object UrlBinders {

  /**
   * Builds a [[PathBindable]] A for a given Enum A
   */
  def pathBinder[A](enum: Enum[A]): PathBindable[A] = new PathBindable[A] {
    def unbind(key: String, value: A): String = value.toString
    def bind(key: String, value: String): Either[String, A] = {
      enum.withNameOption(value) match {
        case Some(v) => Right(v)
        case _ => Left(s"Unknown value supplied for $enum '" + value + "'")
      }
    }
  }

  /**
   * Builds a [[QueryStringBindable]] A for a given Enum A
   */
  def queryBinder[A](enum: Enum[A]): QueryStringBindable[A] = new Parsing[A](
    enum.withName,
    _.toString,
    (key, exception) => "Cannot parse parameter %s as an Enum: %s".format(key, exception.getMessage)
  )

}
