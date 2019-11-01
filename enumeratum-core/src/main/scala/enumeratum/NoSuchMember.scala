package enumeratum

import scala.util.control.NoStackTrace

final case class NoSuchMember[A <: EnumEntry](notFoundName: String, enumValues: IndexedSeq[A])
    extends NoSuchElementException
    with NoStackTrace {
  override def getMessage: String =
    s"$notFoundName is not a member of Enum (${enumValues.map(_.entryName).mkString(", ")})"
}
