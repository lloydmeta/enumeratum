package enumeratum

import java.util
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import testing._

/**
  * Created by Lloyd on 2/6/17.
  *
  * Copyright 2017
  */
/**
  * Compares performance of Java's EnumSet, Scala's Set for JavaEnums and Enumeratum Enums
  */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@SuppressWarnings(Array("org.wartremover.warts.Var"))
class SetComparisons {

  private val jEnumEnumSet = util.EnumSet.allOf(classOf[JAgeGroup])

  private val jEnumEnumsetSmall = util.EnumSet.of(JAgeGroup.Adult)
  private val jEnumEnumsetMedium =
    util.EnumSet.of(JAgeGroup.Adult, JAgeGroup.Baby, JAgeGroup.Senior)

  private val jEnumScalaSet = Set(JAgeGroup.values(): _*)

  private val enumeratumScalaSet = Set(AgeGroup.values: _*)

  // Don't reuse the previous set for All because subsetOf optimises w/ referential eq check :p
  private val enumeratumScalaAll                  = Set(AgeGroup.values: _*)
  private val enumeratumScalaSmall: Set[AgeGroup] = Set(AgeGroup.Adult)
  private val enumeratumScalaMedium: Set[AgeGroup] =
    Set(AgeGroup.Adult, AgeGroup.Baby, AgeGroup.Senior)

  private def randomFrom[A](seq: Seq[A]): A = {
    seq(scala.util.Random.nextInt(seq.size))
  }

  private var jAgeGroupEnum: JAgeGroup = _
  private var ageGroupEnum: AgeGroup   = _

  @Setup(Level.Trial)
  def setup(): Unit = {
    jAgeGroupEnum = randomFrom(JAgeGroup.values())
    ageGroupEnum = randomFrom(AgeGroup.values)
  }

  @Benchmark
  def jEnumEnumSetContains(bh: Blackhole): Unit = bh.consume {
    jEnumEnumSet.contains(jAgeGroupEnum)
  }

  @Benchmark
  def jEnumEnumSetContainsAllSmall(bh: Blackhole): Unit = bh.consume {
    jEnumEnumSet.containsAll(jEnumEnumsetSmall)
  }

  @Benchmark
  def jEnumEnumSetContainsAllMedium(bh: Blackhole): Unit = bh.consume {
    jEnumEnumSet.containsAll(jEnumEnumsetMedium)
  }

  @Benchmark
  def jEnumEnumSetContainsAllAll(bh: Blackhole): Unit = bh.consume {
    jEnumEnumSet.containsAll(jEnumEnumSet)
  }

  @Benchmark
  def jEnumScalaSetContains(bh: Blackhole): Unit = bh.consume {
    jEnumScalaSet.contains(jAgeGroupEnum)
  }

  @Benchmark
  def enumeratumScalaSetContains(bh: Blackhole): Unit = bh.consume {
    enumeratumScalaSet.contains(ageGroupEnum)
  }

  @Benchmark
  def enumeratumScalaSetSubsetOfSmall(bh: Blackhole): Unit = bh.consume {
    enumeratumScalaSmall.subsetOf(enumeratumScalaSet)
  }

  @Benchmark
  def enumeratumScalaSetSubsetOfMedium(bh: Blackhole): Unit = bh.consume {
    enumeratumScalaMedium.subsetOf(enumeratumScalaSet)
  }

  @Benchmark
  def enumeratumScalaSetSubsetOfAll(bh: Blackhole): Unit = bh.consume {
    enumeratumScalaAll.subsetOf(enumeratumScalaSet)
  }

}
