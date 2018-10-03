package enumeratum
import cats.instances.string._
import cats.{Eq, Hash, Show}

object Cats {

  /**
  * Builds an `Eq` instance which differentiates all enum values as it's based on universal equals.
  */
  def eqForEnum[A <: EnumEntry]: Eq[A] = Eq.fromUniversalEquals[A]

  /**
  * Builds a `Show` instance returning the entry name (respecting possible mixins).
  */
  def showForEnum[A <: EnumEntry]: Show[A] = Show.show[A](_.entryName)

  /**
  * `Hash` instance based on the entry name.
  */
  def hashForEnum[A <: EnumEntry]: Hash[A] = Hash.by[A, String](_.entryName)

}
