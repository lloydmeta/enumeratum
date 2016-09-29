package enumeratum

import upickle.default.{Writer, Reader}
import EnrichedPartialFunction._

object UPickler {

  /**
    * Returns a UPickle [[Reader]] for a given [[Enum]]
    *
    * @param enum the enum you wish to make a Reader for
    * @param insensitive whether or not to match case-insensitively
    */
  def reader[A <: EnumEntry](enum: Enum[A], insensitive: Boolean = false): Reader[A] =
    Reader[A] {
      val stringReader = implicitly[Reader[String]]
      val memberFinder: String => Option[A] =
        if (insensitive) enum.withNameInsensitiveOption
        else enum.withNameOption
      val pfMaybeMemberToMember: PartialFunction[Option[A], A] = {
        case Some(a) => a
      }
      stringReader.read.andThen(memberFinder).andThenPartial(pfMaybeMemberToMember)
    }

  /**
    * Returns a UPickle [[Reader]] for a given [[Enum]]
    *
    * @param enum the enum you wish to make a Reader for transformed to lower case
    */
  def readerLowercaseLower[A <: EnumEntry](enum: Enum[A]): Reader[A] =
    Reader[A] {
      val stringReader = implicitly[Reader[String]]
      val pfMaybeMemberToMember: PartialFunction[Option[A], A] = {
        case Some(a) => a
      }
      stringReader.read
        .andThen(enum.withNameLowercaseOnlyOption)
        .andThenPartial(pfMaybeMemberToMember)
    }

  /**
    * Returns a UPickle [[Reader]] for a given [[Enum]]
    *
    * @param enum the enum you wish to make a Reader for transformed to upper case
    */
  def readerUppercaseOnly[A <: EnumEntry](enum: Enum[A]): Reader[A] =
    Reader[A] {
      val stringReader = implicitly[Reader[String]]
      val pfMaybeMemberToMember: PartialFunction[Option[A], A] = {
        case Some(a) => a
      }
      stringReader.read
        .andThen(enum.withNameUppercaseOnlyOption)
        .andThenPartial(pfMaybeMemberToMember)
    }

  /**
    * Returns a [[Writer]] for a given [[Enum]]
    *
    * @param enum [[Enum]] to make a [[Writer]] for
    */
  def writer[A <: EnumEntry](enum: Enum[A]): Writer[A] = {
    val stringWriter = implicitly[Writer[String]]
    Writer[A] {
      case member => stringWriter.write(member.entryName)
    }
  }

  /**
    * Returns a [[Writer]] for a given [[Enum]], outputted as lower case
    *
    * @param enum [[Enum]] to make a [[Writer]] for
    */
  def writerLowercaseOnly[A <: EnumEntry](enum: Enum[A]): Writer[A] = {
    val stringWriter = implicitly[Writer[String]]
    Writer[A] {
      case member => stringWriter.write(member.entryName.toLowerCase)
    }
  }

  /**
    * Returns a [[Writer]] for a given [[Enum]], outputted as upper case
    *
    * @param enum [[Enum]] to make a [[Writer]] for
    */
  def writerUppercaseOnly[A <: EnumEntry](enum: Enum[A]): Writer[A] = {
    val stringWriter = implicitly[Writer[String]]
    Writer[A] {
      case member => stringWriter.write(member.entryName.toUpperCase)
    }
  }

}
