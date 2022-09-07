package enumeratum.values

import play.api.data.{Mapping, Forms => PlayForms}
import play.api.data.format.Formatter

private[values] trait FormsCompat { _: Forms.type =>

  /** Returns a [[ValueEnum]] mapping for Play form fields
    */
  @deprecated("Use `enumMapping`", "1.7.2")
  def enum[ValueType, EntryType <: ValueEnumEntry[ValueType], EnumType <: ValueEnum[
    ValueType,
    EntryType
  ]](baseFormatter: Formatter[ValueType])(enum: EnumType): Mapping[EntryType] =
    PlayForms.of(formatter(baseFormatter)(enum))

  @inline def enumMapping[ValueType, EntryType <: ValueEnumEntry[ValueType], EnumType <: ValueEnum[
    ValueType,
    EntryType
  ]](baseFormatter: Formatter[ValueType])(e: EnumType): Mapping[EntryType] =
    enum[ValueType, EntryType, EnumType](baseFormatter)(e)
}
