package enumeratum.values

/** Created by Lloyd on 4/12/16.
  *
  * Copyright 2016
  */
sealed abstract class ContentType(val value: Long, name: String) extends LongEnumEntry

case object ContentType extends LongEnum[ContentType] {

  case object Text  extends ContentType(value = 1L, name = "text")
  case object Image extends ContentType(value = 2L, name = "image")
  case object Video extends ContentType(value = 3L, name = "video")
  case object Audio extends ContentType(value = 4L, name = "audio")

  val values = findValues

}

case object Papyrus extends ContentType(5, "papyrus")
