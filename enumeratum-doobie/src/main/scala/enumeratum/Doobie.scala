package enumeratum

import org.typelevel.doobie.util._
import org.typelevel.doobie.Meta

object Doobie {

  /** Returns an Encoder for the given enum
    */
  def meta[A <: EnumEntry](@deprecatedName(Symbol("enum")) e: Enum[A]): Meta[A] =
    Meta[String].timap(e.withName)(_.entryName)
}
