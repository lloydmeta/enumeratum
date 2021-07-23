package enumeratum

import java.util
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import testing.JAgeGroup

/** Created by Lloyd on 2/6/17.
  *
  * Copyright 2017
  */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@SuppressWarnings(Array("org.wartremover.warts.Var"))
class MapComparisons {

  private val jEnumEnumMap = {
    val m: util.EnumMap[JAgeGroup, String] = new util.EnumMap(classOf[JAgeGroup])
    JAgeGroup.values().foreach(e => m.put(e, e.name()))
    m
  }

  private val jEnumScalaMap = Map(JAgeGroup.values().map(e => e -> e.name()): _*)

  private val ageGroupScalaMap = Map(AgeGroup.values.map(e => e -> e.entryName): _*)

  private def randomFrom[A](seq: Seq[A]): A = {
    seq(scala.util.Random.nextInt(seq.size))
  }

  private var jEnum: JAgeGroup       = _
  private var ageGroupEnum: AgeGroup = _

  @Setup(Level.Trial)
  def setup(): Unit = {
    jEnum = randomFrom(JAgeGroup.values())
    ageGroupEnum = randomFrom(AgeGroup.values)
  }

  @Benchmark
  def jEnumEnumMapGet(bh: Blackhole): Unit = bh.consume {
    jEnumEnumMap.get(jEnum)
  }

  @Benchmark
  def jEnumScalaMapGet(bh: Blackhole): Unit = bh.consume {
    jEnumScalaMap.get(jEnum)
  }

  @Benchmark
  def enumeratumScalaMapGet(bh: Blackhole): Unit = bh.consume {
    ageGroupScalaMap.get(ageGroupEnum)
  }

}
