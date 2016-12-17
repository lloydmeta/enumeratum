package enumeratum

import play.api.data.format.Formatter
import play.api.data.{FormError, Forms => PlayForms, Mapping}

/**
  * Created by Lloyd on 2/3/15.
  */
object Forms {

  /**
    * Returns an [[Enum]] mapping
    *
    * Example:
    *
    * {{{
    * scala> import enumeratum._
    * scala> import play.api.data.Form
    *
    * scala> sealed trait Greeting extends EnumEntry
    *
    * scala> object Greeting extends Enum[Greeting] {
    *      |   val values = findValues
    *      |   case object Hello   extends Greeting
    *      |   case object GoodBye extends Greeting
    *      |   case object Hi      extends Greeting
    *      |   case object Bye     extends Greeting
    *      | }
    *
    * scala> val form = Form("greeting" -> Forms.enum(Greeting))
    * scala> form.bind(Map("greeting" -> "Hello")).value
    * res0: Option[Greeting] = Some(Hello)
    *
    * scala> val formInsensitive = Form("greeting" -> Forms.enum(Greeting, true))
    * scala> formInsensitive.bind(Map("greeting" -> "hElLo")).value
    * res1: Option[Greeting] = Some(Hello)
    * }}}
    *
    * @param enum The enum
    * @param insensitive bind in a case-insensitive way, defaults to false
    */
  def enum[A <: EnumEntry](enum: Enum[A], insensitive: Boolean = false): Mapping[A] =
    PlayForms.of(format(enum, insensitive))

  /**
    * Returns an [[Enum]] mapping for lower case binding only
    *
    * Example:
    *
    * {{{
    * scala> import enumeratum._
    * scala> import play.api.data.Form
    *
    * scala> sealed trait Greeting extends EnumEntry
    *
    * scala> object Greeting extends Enum[Greeting] {
    *      |   val values = findValues
    *      |   case object Hello   extends Greeting
    *      |   case object GoodBye extends Greeting
    *      |   case object Hi      extends Greeting
    *      |   case object Bye     extends Greeting
    *      | }
    *
    * scala> val form = Form("greeting" -> Forms.enumLowerCaseOnly(Greeting))
    * scala> form.bind(Map("greeting" -> "hello")).value
    * res0: Option[Greeting] = Some(Hello)
    *
    * scala> form.bind(Map("greeting" -> "Hello")).value
    * res1: Option[Greeting] = None
    * }}}
    *
    * @param enum The enum
    */
  def enumLowerCaseOnly[A <: EnumEntry](enum: Enum[A]): Mapping[A] =
    PlayForms.of(formatLowercaseOnly(enum))

  /**
    * Returns an [[Enum]] mapping for upper case binding only
    *
    * Example:
    *
    * {{{
    * scala> import enumeratum._
    * scala> import play.api.data.Form
    *
    * scala> sealed trait Greeting extends EnumEntry
    *
    * scala> object Greeting extends Enum[Greeting] {
    *      |   val values = findValues
    *      |   case object Hello   extends Greeting
    *      |   case object GoodBye extends Greeting
    *      |   case object Hi      extends Greeting
    *      |   case object Bye     extends Greeting
    *      | }
    *
    * scala> val form = Form("greeting" -> Forms.enumUppercaseOnly(Greeting))
    * scala> form.bind(Map("greeting" -> "HELLO")).value
    * res0: Option[Greeting] = Some(Hello)
    *
    * scala> form.bind(Map("greeting" -> "Hello")).value
    * res1: Option[Greeting] = None
    * }}}
    *
    * @param enum The enum
    */
  def enumUppercaseOnly[A <: EnumEntry](enum: Enum[A]): Mapping[A] =
    PlayForms.of(formatUppercaseOnly(enum))

  /**
    * Returns a Formatter for [[Enum]]
    *
    * @param enum The enum
    * @param insensitive bind in a case-insensitive way, defaults to false
    */
  def format[A <: EnumEntry](enum: Enum[A], insensitive: Boolean = false): Formatter[A] =
    new Formatter[A] {
      def bind(key: String, data: Map[String, String]) = {
        play.api.data.format.Formats.stringFormat.bind(key, data).right.flatMap { s =>
          val maybeBound =
            if (insensitive) enum.withNameInsensitiveOption(s)
            else enum.withNameOption(s)
          maybeBound match {
            case Some(obj) => Right(obj)
            case None      => Left(Seq(FormError(key, "error.enum", Nil)))
          }
        }
      }
      def unbind(key: String, value: A) = Map(key -> value.entryName)
    }

  /**
    * Returns a Formatter for [[Enum]] that transforms to lower case
    *
    * @param enum The enum
    */
  def formatLowercaseOnly[A <: EnumEntry](enum: Enum[A]): Formatter[A] =
    new Formatter[A] {
      def bind(key: String, data: Map[String, String]) = {
        play.api.data.format.Formats.stringFormat.bind(key, data).right.flatMap { s =>
          enum.withNameLowercaseOnlyOption(s) match {
            case Some(obj) => Right(obj)
            case None      => Left(Seq(FormError(key, "error.enum", Nil)))
          }
        }
      }
      def unbind(key: String, value: A) =
        Map(key -> value.entryName.toLowerCase)
    }

  /**
    * Returns a Formatter for [[Enum]] that transforms to upper case
    *
    * @param enum The enum
    */
  def formatUppercaseOnly[A <: EnumEntry](enum: Enum[A]): Formatter[A] =
    new Formatter[A] {
      def bind(key: String, data: Map[String, String]) = {
        play.api.data.format.Formats.stringFormat.bind(key, data).right.flatMap { s =>
          enum.withNameUppercaseOnlyOption(s) match {
            case Some(obj) => Right(obj)
            case None      => Left(Seq(FormError(key, "error.enum", Nil)))
          }
        }
      }
      def unbind(key: String, value: A) =
        Map(key -> value.entryName.toUpperCase)
    }

}
