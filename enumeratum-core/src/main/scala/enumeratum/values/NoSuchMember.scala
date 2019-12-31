package enumeratum.values

import scala.util.control.NoStackTrace

final case class NoSuchMember[ValueType, A <: ValueEnumEntry[ValueType]](notFoundValue: ValueType,
                                                                         enumValues: IndexedSeq[A])
    extends NoSuchElementException
    with NoStackTrace {
  override def getMessage: String =
    s"$notFoundValue is not a member of ValueEnum (${enumValues.map(_.value).mkString(", ")})"
}
