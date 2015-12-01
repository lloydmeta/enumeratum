package enumeratum

import scala.collection.mutable

/**
  * Based on [[java.util.EnumSet]].
  * Since the key universe is small and mappable to a compact integer range, the set can be represented with a bitset.
  *
  * The main difference is that enumeratum's Enum entries don't know their index (for now), so most operations are O(n)
  * - which might be acceptable for you
  */
class MutableEnumSet[K <: EnumEntry](enum: Enum[K]) extends scala.collection.mutable.Set[K] {
  private[this] val keyUniverse = new mutable.BitSet(enum.values.length)

  // O(n)
  def +=(elem: K) = {
    keyUniverse.add(enum.indexOf(elem))
    this
  }

  // O(n)
  def -=(elem: K) = {
    keyUniverse.remove(enum.indexOf(elem))
    this
  }

  // O(1)
  def iterator = keyUniverse.iterator.map(enum.values)

  // O(n)
  def contains(elem: K) = keyUniverse(enum.indexOf(elem))

  // O(n)
  override def size: Int = keyUniverse.size

  override def clear(): Unit = keyUniverse.clear()
}

class ImmutableEnumSet[K <: EnumEntry](enum: Enum[K])(entries: K*) extends scala.collection.immutable.Set[K] {
  private[this] val itsMutableInside = new MutableEnumSet[K](enum)
  entries.foreach(itsMutableInside.+=)

  def contains(elem: K) = itsMutableInside.contains(elem)

  def +(elem: K) = new ImmutableEnumSet[K](enum)(entries :+ elem: _*)

  def -(elem: K) = new ImmutableEnumSet[K](enum)(entries.filterNot(elem.==): _*)

  def iterator = itsMutableInside.iterator

  override def size: Int = itsMutableInside.size
}
