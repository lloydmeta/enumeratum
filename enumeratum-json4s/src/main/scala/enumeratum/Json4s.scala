package enumeratum

import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object Json4s {

  def serializer[A <: EnumEntry: Manifest](enum: Enum[A]): CustomSerializer[A] = new CustomSerializer[A](_ => (
    {
      case JString(s) if enum.withNameOption(s).isDefined => enum.withName(s)
    },
    {
      case x: A => JString(x.entryName)
    }
  ))

}
