package enumeratum.values

import play.api.data.format.Formatter
import play.api.data.{ FormError, Mapping, Forms => PlayForms }

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */
object Forms {

  /**
   * Returns a [[ValueEnum]] mapping for Play form fields
   */
  def enum[ValueType, EntryType <: ValueEnumEntry[ValueType], EnumType <: ValueEnum[ValueType, EntryType]](baseFormatter: Formatter[ValueType])(enum: EnumType): Mapping[EntryType] = {
    PlayForms.of(formatter(baseFormatter)(enum))
  }

  private[this] def formatter[ValueType, EntryType <: ValueEnumEntry[ValueType], EnumType <: ValueEnum[ValueType, EntryType]](baseFormatter: Formatter[ValueType])(enum: EnumType) = {
    new Formatter[EntryType] {
      def bind(key: String, data: Map[String, String]): Either[Seq[FormError], EntryType] = baseFormatter.bind(key, data).right.flatMap { s =>
        val maybeBound = enum.withValueOpt(s)
        maybeBound match {
          case Some(obj) => Right(obj)
          case None => Left(Seq(FormError(key, "error.enum", Nil)))
        }
      }

      def unbind(key: String, value: EntryType): Map[String, String] = Map(key -> value.value.toString)
    }
  }

}
