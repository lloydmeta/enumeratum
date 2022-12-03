package enumeratum.values

import play.api.libs.json._

/** Created by Lloyd on 4/13/16.
  *
  * Copyright 2016
  */
object EnumFormats {

  /** Returns a Reads for the provided ValueEnum based on the given base Reads for the Enum's value
    * type
    */
  def reads[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      @deprecatedName(Symbol("enum")) e: ValueEnum[ValueType, EntryType]
  )(implicit
      baseReads: Reads[ValueType]
  ): Reads[EntryType] =
    new Reads[EntryType] {
      def reads(json: JsValue): JsResult[EntryType] =
        baseReads.reads(json).flatMap { s =>
          e.withValueOpt(s) match {
            case Some(obj) => JsSuccess(obj)
            case None      => JsError("error.expected.validenumvalue")
          }
        }
    }

  /** Returns a Writes for the provided ValueEnum based on the given base Writes for the Enum's
    * value type
    */
  def writes[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      @deprecatedName(Symbol("enum")) e: ValueEnum[ValueType, EntryType]
  )(implicit
      baseWrites: Writes[ValueType]
  ): Writes[EntryType] = Writes[EntryType] { o => baseWrites.writes(o.value) }

  /** Returns a Formats for the provided ValueEnum based on the given base Reads and Writes for the
    * Enum's value type
    */
  def formats[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      @deprecatedName(Symbol("enum")) e: ValueEnum[ValueType, EntryType]
  )(implicit baseReads: Reads[ValueType], baseWrites: Writes[ValueType]): Format[EntryType] = {
    Format(reads(e), writes(e))
  }

  /** Format for Char
    */
  implicit val charFormat: Format[Char] = new Format[Char] {
    def writes(o: Char): JsValue = JsString(o.toString)

    def reads(json: JsValue): JsResult[Char] = json match {
      case JsString(s) if s.length == 1 => JsSuccess(s.charAt(0))
      case _                            => JsError("error.expected.singleChar")
    }
  }

}
