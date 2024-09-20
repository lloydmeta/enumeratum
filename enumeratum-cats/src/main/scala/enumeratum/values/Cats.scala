package enumeratum.values
import cats.{Eq, Hash, Order, Show}
import cats.syntax.contravariant._

object Cats {

  /** Builds an `Eq` instance which differentiates all enum values as it's based on universal
    * equals.
    */
  def eqForEnum[A <: ValueEnumEntry[?]]: Eq[A] = Eq.fromUniversalEquals[A]

  /** Builds an `Eq` instance which acts accordingly to the given `Eq` on the value type. Allows to
    * implement different behaviour than [[eqForEnum]], for example grouping several enum values in
    * special contexts.
    */
  def valueEqForEnum[A <: ValueEnumEntry[V], V: Eq]: Eq[A] = Eq.by[A, V](_.value)

  /** Builds a `Show` instance based on `toString`.
    */
  def showForEnum[A <: ValueEnumEntry[?]]: Show[A] = Show.fromToString[A]

  /** Builds a `Show` instance from the given `Show` on the value type.
    */
  def valueShowForEnum[A <: ValueEnumEntry[V], V: Show]: Show[A] = Show[V].contramap[A](_.value)

  /** Builds a `Order` instance from the given `Order` on the value type.
    */
  def valueOrderForEnum[A <: ValueEnumEntry[V], V: Order]: Order[A] = Order.by[A, V](_.value)

  /** Builds a `Hash` instance from the given `Hash` on the value type.
    */
  def valueOrderForEnum[A <: ValueEnumEntry[V], V: Hash]: Hash[A] = Hash.by[A, V](_.value)

}
