package enumeratum

import play.api.mvc.PathBindable
import play.api.mvc.QueryStringBindable

/** Created by Lloyd on 2/3/15.
  */
object UrlBinders {

  /** Builds a [[PathBindable]] A for a given Enum A
    *
    * @param e
    *   The enum
    * @param insensitive
    *   bind in a case-insensitive way, defaults to false
    */
  def pathBinder[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A],
      insensitive: Boolean = false
  ): PathBindable[A] =
    new PathBindable[A] {
      def unbind(key: String, value: A): String = value.entryName
      def bind(key: String, value: String): Either[String, A] = {
        val maybeBound =
          if (insensitive) e.withNameInsensitiveOption(value)
          else e.withNameOption(value)
        maybeBound match {
          case Some(v) => Right(v)
          case _       => Left(s"Unknown value supplied for ${e.toString} '$value'")
        }
      }
    }

  /** Builds a [[PathBindable]] A for a given Enum A that transforms to lower case
    *
    * @param e
    *   The enum
    */
  def pathBinderLowercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): PathBindable[A] =
    new PathBindable[A] {
      def unbind(key: String, value: A): String = value.entryName.toLowerCase
      def bind(key: String, value: String): Either[String, A] = {
        e.withNameLowercaseOnlyOption(value) match {
          case Some(v) => Right(v)
          case _       => Left(s"Unknown value supplied for ${e.toString} '$value'")
        }
      }
    }

  /** Builds a [[PathBindable]] A for a given Enum A that transforms to upper case
    *
    * @param e
    *   The enum
    */
  def pathBinderUppercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): PathBindable[A] =
    new PathBindable[A] {
      def unbind(key: String, value: A): String = value.entryName.toUpperCase
      def bind(key: String, value: String): Either[String, A] = {
        e.withNameUppercaseOnlyOption(value) match {
          case Some(v) => Right(v)
          case _       => Left(s"Unknown value supplied for ${e.toString} '$value'")
        }
      }
    }

  /** Builds a [[QueryStringBindable]] A for a given Enum A
    *
    * @param e
    *   The enum
    * @param insensitive
    *   bind in a case-insensitive way, defaults to false
    */
  def queryBinder[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A],
      insensitive: Boolean = false
  ): QueryStringBindable[A] =
    new QueryStringBindable[A] {

      def unbind(key: String, value: A): String = s"$key=${value.entryName}"

      def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, A]] = {
        params.get(key).flatMap(_.headOption).map { p =>
          val maybeBound = {
            if (insensitive) e.withNameInsensitiveOption(p)
            else e.withNameOption(p)
          }

          maybeBound match {
            case Some(v) => Right(v)
            case _       => Left(s"Cannot parse parameter $key as an Enum: ${e.toString}")
          }
        }
      }
    }

  /** Builds a [[QueryStringBindable]] A for a given Enum A that transforms to lower case
    *
    * @param e
    *   The enum
    */
  def queryBinderLowercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): QueryStringBindable[A] =
    new QueryStringBindable[A] {

      def unbind(key: String, value: A): String =
        s"$key=${value.entryName.toLowerCase}"

      def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, A]] = {
        params.get(key).flatMap(_.headOption).map { p =>
          e.withNameLowercaseOnlyOption(p) match {
            case Some(v) => Right(v)
            case _       => Left(s"Cannot parse parameter $key as an Enum: ${e.toString}")
          }
        }
      }
    }

  /** Builds a [[QueryStringBindable]] A for a given Enum A that transforms to upper case
    *
    * @param e
    *   The enum
    */
  def queryBinderUppercaseOnly[A <: EnumEntry](
      @deprecatedName(Symbol("enum")) e: Enum[A]
  ): QueryStringBindable[A] =
    new QueryStringBindable[A] {

      def unbind(key: String, value: A): String =
        s"$key=${value.entryName.toUpperCase}"

      def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, A]] = {
        params.get(key).flatMap(_.headOption).map { p =>
          e.withNameUppercaseOnlyOption(p) match {
            case Some(v) => Right(v)
            case _       => Left(s"Cannot parse parameter $key as an Enum: ${e.toString}")
          }
        }
      }
    }

}
