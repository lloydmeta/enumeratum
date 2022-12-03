package enumeratum

import play.api.data.{Mapping, Forms => PlayForms}

private[enumeratum] trait FormsCompat { _: Forms.type =>

  @deprecated("Use `enumMapping`", "1.7.2")
  def enum[A <: EnumEntry](
      enum: Enum[A],
      insensitive: Boolean = false
  ): Mapping[A] = PlayForms.of(format(enum, insensitive))

  /** Returns an [[Enum]] mapping
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
    *     |   val values = findValues
    *     |   case object Hello   extends Greeting
    *     |   case object GoodBye extends Greeting
    *     |   case object Hi      extends Greeting
    *     |   case object Bye     extends Greeting
    *     | }
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
    * @param e
    *   The enum
    * @param insensitive
    *   bind in a case-insensitive way, defaults to false
    */
  @inline def enumMapping[A <: EnumEntry](
      e: Enum[A],
      insensitive: Boolean = false
  ): Mapping[A] = enum[A](e, insensitive)

}
