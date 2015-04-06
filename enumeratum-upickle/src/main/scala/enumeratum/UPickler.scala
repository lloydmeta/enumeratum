package enumeratum

import upickle.{ Js, Writer, Reader }

object UPickler {

  /**
   * Returns a UPickle [[Reader]] for a given [[Enum]]
   *
   * @param enum the enum you wish to make a Reader for
   * @param insensitive whether or not to match case-insensitively
   */
  def reader[A](enum: Enum[A], insensitive: Boolean = false): Reader[A] = {
    Reader[A] {
      val memberFinder: String => Option[A] = if (insensitive) enum.withNameInsensitiveOption else enum.withNameOption
      val pfIfJsStr: PartialFunction[Js.Value, String] = {
        case Js.Str(s) => s
      }
      val pfMaybeMember = pfIfJsStr.andThen(memberFinder)
      val pfMaybeMemberToMember: PartialFunction[Option[A], A] = {
        case Some(a) => a
      }
      andThenPartial(pfMaybeMember, pfMaybeMemberToMember)
    }
  }

  /**
   * Returns a [[Writer]] for a given [[Enum]]
   *
   * @param enum [[Enum]] to make a [[Writer]] for
   */
  def writer[A](enum: Enum[A]): Writer[A] = Writer[A] {
    case member => Js.Str(member.toString)
  }

  /**
   * Private helper for composing PartialFunctions
   *
   * Stolen from http://stackoverflow.com/questions/23024626/compose-partial-functions
   */
  private def andThenPartial[A, B, C](pf1: PartialFunction[A, B], pf2: PartialFunction[B, C]): PartialFunction[A, C] = {
    Function.unlift(pf1.lift(_) flatMap pf2.lift)
  }

}