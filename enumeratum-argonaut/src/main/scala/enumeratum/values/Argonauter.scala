package enumeratum.values

import argonaut._
import Argonaut._

/**
  * Created by alonsodomin on 14/10/2016.
  */
object Argonauter {

  def encoder[ValueType: EncodeJson, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]): EncodeJson[EntryType] = {
    val encodeValue = implicitly[EncodeJson[ValueType]]
    EncodeJson { entry =>
      encodeValue(entry.value)
    }
  }

  def decoder[ValueType: DecodeJson, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]): DecodeJson[EntryType] = {
    val decodeValue = implicitly[DecodeJson[ValueType]]
    DecodeJson { cursor =>
      decodeValue(cursor).flatMap { value =>
        enum.withValueOpt(value) match {
          case Some(entry) => okResult(entry)
          case _           => failResult(s"$value is not a member of enum $enum", cursor.history)
        }
      }
    }
  }

}
