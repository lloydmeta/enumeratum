package enumeratum.values

import play.api.data.format.Formatter
import play.api.data.{FormError, Mapping, Forms => PlayForms}

/** Created by Lloyd on 4/13/16.
  *
  * Copyright 2016
  */
object Forms extends FormsCompat {

  protected[this] def formatter[ValueType, EntryType <: ValueEnumEntry[
    ValueType
  ], EnumType <: ValueEnum[ValueType, EntryType]](
      baseFormatter: Formatter[ValueType]
  )(@deprecatedName(Symbol("enum")) e: EnumType) = {
    new Formatter[EntryType] {
      def bind(key: String, data: Map[String, String]): Either[Seq[FormError], EntryType] =
        baseFormatter.bind(key, data).right.flatMap { s =>
          e.withValueOpt(s) match {
            case Some(obj) => Right(obj)
            case None      => Left(Seq(FormError(key, "error.enum", Nil)))
          }
        }

      def unbind(key: String, value: EntryType): Map[String, String] =
        baseFormatter.unbind(key, value.value)
    }
  }

  /** Taken from Play 2.4.x implementation
    */
  private[values] val charFormatter: Formatter[Char] = new Formatter[Char] {
    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Char] = {
      val maybeChar: Option[Either[Seq[FormError], Char]] =
        data.get(key).filter(s => s.length == 1 && s != " ").map(s => Right(s.charAt(0)))
      maybeChar.getOrElse(Left(Seq(FormError(key, "error.required", Nil))))
    }
    def unbind(key: String, value: Char): Map[String, String] = Map(key -> value.toString)
  }
}
