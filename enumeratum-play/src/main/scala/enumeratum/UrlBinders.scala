package enumeratum

import play.api.mvc.PathBindable
import play.api.mvc.QueryStringBindable

/**
  * Created by Lloyd on 2/3/15.
  */
object UrlBinders {

  /**
    * Builds a [[PathBindable]] A for a given Enum A
    *
    * @param enum The enum
    * @param insensitive bind in a case-insensitive way, defaults to false
    */
  def pathBinder[A <: EnumEntry](enum: Enum[A], insensitive: Boolean = false): PathBindable[A] =
    new PathBindable[A] {
      def unbind(key: String, value: A): String = value.entryName
      def bind(key: String, value: String): Either[String, A] = {
        val maybeBound =
          if (insensitive) enum.withNameInsensitiveOption(value)
          else enum.withNameOption(value)
        maybeBound match {
          case Some(v) => Right(v)
          case _       => Left(s"Unknown value supplied for ${enum.toString} '$value'")
        }
      }
    }

  /**
    * Builds a [[PathBindable]] A for a given Enum A that transforms to lower case
    *
    * @param enum The enum
    */
  def pathBinderLowercaseOnly[A <: EnumEntry](enum: Enum[A]): PathBindable[A] =
    new PathBindable[A] {
      def unbind(key: String, value: A): String = value.entryName.toLowerCase
      def bind(key: String, value: String): Either[String, A] = {
        enum.withNameLowercaseOnlyOption(value) match {
          case Some(v) => Right(v)
          case _       => Left(s"Unknown value supplied for ${enum.toString} '$value'")
        }
      }
    }

  /**
    * Builds a [[PathBindable]] A for a given Enum A that transforms to upper case
    *
    * @param enum The enum
    */
  def pathBinderUppercaseOnly[A <: EnumEntry](enum: Enum[A]): PathBindable[A] =
    new PathBindable[A] {
      def unbind(key: String, value: A): String = value.entryName.toUpperCase
      def bind(key: String, value: String): Either[String, A] = {
        enum.withNameUppercaseOnlyOption(value) match {
          case Some(v) => Right(v)
          case _       => Left(s"Unknown value supplied for ${enum.toString} '$value'")
        }
      }
    }

  /**
    * Builds a [[QueryStringBindable]] A for a given Enum A
    *
    * @param enum The enum
    * @param insensitive bind in a case-insensitive way, defaults to false
    */
  def queryBinder[A <: EnumEntry](
      enum: Enum[A],
      insensitive: Boolean = false
  ): QueryStringBindable[A] =
    new QueryStringBindable[A] {

      def unbind(key: String, value: A): String = s"$key=${value.entryName}"

      def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, A]] = {
        params.get(key).flatMap(_.headOption).map { p =>
          val maybeBound =
            if (insensitive) enum.withNameInsensitiveOption(p)
            else enum.withNameOption(p)
          maybeBound match {
            case Some(v) => Right(v)
            case _       => Left(s"Cannot parse parameter $key as an Enum: ${this.toString}")
          }
        }
      }
    }

  /**
    * Builds a [[QueryStringBindable]] A for a given Enum A that transforms to lower case
    *
    * @param enum The enum
    */
  def queryBinderLowercaseOnly[A <: EnumEntry](enum: Enum[A]): QueryStringBindable[A] =
    new QueryStringBindable[A] {

      def unbind(key: String, value: A): String =
        s"$key=${value.entryName.toLowerCase}"

      def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, A]] = {
        params.get(key).flatMap(_.headOption).map { p =>
          enum.withNameLowercaseOnlyOption(p) match {
            case Some(v) => Right(v)
            case _       => Left(s"Cannot parse parameter $key as an Enum: ${this.toString}")
          }
        }
      }
    }

  /**
    * Builds a [[QueryStringBindable]] A for a given Enum A that transforms to upper case
    *
    * @param enum The enum
    */
  def queryBinderUppercaseOnly[A <: EnumEntry](enum: Enum[A]): QueryStringBindable[A] =
    new QueryStringBindable[A] {

      def unbind(key: String, value: A): String =
        s"$key=${value.entryName.toUpperCase}"

      def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, A]] = {
        params.get(key).flatMap(_.headOption).map { p =>
          enum.withNameUppercaseOnlyOption(p) match {
            case Some(v) => Right(v)
            case _       => Left(s"Cannot parse parameter $key as an Enum: ${this.toString}")
          }
        }
      }
    }

}
