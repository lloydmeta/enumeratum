package enumeratum

/**
  * Based on [[java.util.EnumMap]].
  * Since the key universe is small and mappable to a compact integer range, the map can be represented with an array.
  *
  * The main difference is that enumeratum's Enum entries don't know their index (for now), so most operations are O(n)
  * - which might be acceptable for you
  */
class MutableEnumMap[K <: EnumEntry, V](enum: Enum[K]) extends scala.collection.mutable.Map[K, V] {
  private[this] val keyUniverse = Array.fill[Option[V]](enum.values.length)(None)

  // O(n)
  def +=(kv: (K, V)) = {
    keyUniverse(enum.indexOf(kv._1)) = Some(kv._2)
    this
  }

  // O(n)
  def -=(key: K) = {
    keyUniverse(enum.indexOf(key)) = None
    this
  }

  // O(n)
  def get(key: K) = keyUniverse.apply(enum.indexOf(key))

  // O(1)
  def iterator = keyUniverse.iterator.zipWithIndex.collect {
    case (Some(v), i) => enum.values(i) -> v
  }

  // O(n)
  override def size = keyUniverse.count(_.isDefined)

  // O(n)
  override def clear() = for (i <- 0 to keyUniverse.length) keyUniverse(i) = None
}

class ImmutableEnumMap[K <: EnumEntry, V](enum: Enum[K])(entries: (K, V)*) extends scala.collection.immutable.Map[K, V] {
  private[this] val itsMutableInside = new MutableEnumMap[K, V](enum)
  entries.foreach {
    case (k, v) => itsMutableInside += k -> v
  }

  def +[B1 >: V](kv: (K, B1)) = new ImmutableEnumMap[K, B1](enum)(kv +: entries.filterNot {
    case (k, _) => k == kv._1
  }: _*)

  def get(key: K) = itsMutableInside.get(key)

  def iterator = itsMutableInside.iterator

  def -(key: K) = new ImmutableEnumMap[K, V](enum)(entries.filterNot {
    case (k, _) => k == key
  }: _*)

  // duplicates must not be counted several times
  override def size = itsMutableInside.size

  override def isEmpty = entries.isEmpty
}
