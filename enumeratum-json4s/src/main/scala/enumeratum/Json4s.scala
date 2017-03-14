package enumeratum

import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object Json4s {

  /**
    * Returns a Json [[CustomSerializer]] for the given Enumeratum enum
    *
    * {{{
    * scala> import enumeratum._
    * scala> import org.json4s._
    * scala> import org.json4s.native.Serialization
    *
    * scala> sealed trait ShirtSize extends EnumEntry
    * scala> case object ShirtSize extends Enum[ShirtSize] {
    *      |  case object Small  extends ShirtSize
    *      |  case object Medium extends ShirtSize
    *      |  case object Large  extends ShirtSize
    *      |  val values = findValues
    *      | }
    *
    * scala> implicit val formats = Serialization.formats(NoTypeHints) + Json4s.serializer(ShirtSize)
    *
    * scala> Serialization.write(ShirtSize.values)
    * res0: String = ["Small","Medium","Large"]
    *
    * scala> Serialization.read[Seq[ShirtSize]]("""["Small","Medium","Large"]""") == ShirtSize.values
    * res1: Boolean = true
    * }}}
    *
    * @param enum the enum you want to generate a Json4s serialiser for
    */
  def serializer[A <: EnumEntry: Manifest](enum: Enum[A]): CustomSerializer[A] =
    new CustomSerializer[A](
      _ =>
        (
          {
            case JString(s) if enum.withNameOption(s).isDefined => enum.withName(s)
          }, {
            case x: A => JString(x.entryName)
          }
      ))

}
