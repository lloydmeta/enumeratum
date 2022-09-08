package enumeratum.values

import play.api.data.{Mapping, Forms => PlayForms}
import play.api.data.format.Formatter

private[values] trait FormsCompat { _self: Forms.type =>

  /** Returns a [[ValueEnum]] mapping for Play form fields
    */
  def enumMapping[ValueType, EntryType <: ValueEnumEntry[ValueType], EnumType <: ValueEnum[
    ValueType,
    EntryType
  ]](baseFormatter: Formatter[ValueType])(e: EnumType): Mapping[EntryType] =
    PlayForms.of(formatter(baseFormatter)(e))

}
