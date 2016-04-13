package enumeratum.values

import enumeratum.EnrichedPartialFunction._
import upickle.default.{ Reader, Writer }

/**
 * Created by Lloyd on 4/13/16.
 *
 * Copyright 2016
 */
object UPickler {

  def reader[ValueType <: AnyVal: Reader, EntryType <: ValueEnumEntry[ValueType]](enum: ValueEnum[ValueType, EntryType]): Reader[EntryType] = {
    val valueReader = implicitly[Reader[ValueType]]
    Reader[EntryType] {
      valueReader.read.andThenPartial { case v if enum.withValueOpt(v).isDefined => enum.withValue(v) }
    }
  }

  def writer[ValueType <: AnyVal: Writer, EntryType <: ValueEnumEntry[ValueType]](enum: ValueEnum[ValueType, EntryType]): Writer[EntryType] = {
    val valueWriter = implicitly[Writer[ValueType]]
    Writer[EntryType] {
      case member => valueWriter.write(member.value)
    }
  }

}
