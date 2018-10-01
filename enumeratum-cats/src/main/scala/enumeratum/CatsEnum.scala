package enumeratum

import cats.instances.string._
import cats.{Eq, Hash, Show}

trait CatsEnum[A <: EnumEntry] { this: Enum[A] =>

  implicit val eqInstance: Eq[A] = Eq.fromUniversalEquals[A]

  implicit val showInstance: Show[A] = Show.show(_.entryName)

  implicit val hashInstance: Hash[A] = Hash.by(_.entryName)

}
