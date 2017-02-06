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

  private val jEnumEnumSet  = util.EnumSet.allOf(classOf[JAgeGroup])
  private val jEnumScalaSet = Set(JAgeGroup.values(): _*)

  private val tinyAlphabetEnumScalaSet = Set(AgeGroup.values: _*)

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
  def jEnumScalaSetContains(bh: Blackhole): Unit = bh.consume {
    jEnumScalaSet.contains(jAgeGroupEnum)
  }

  @Benchmark
  def enumeratumScalaSetContains(bh: Blackhole): Unit = bh.consume {
    tinyAlphabetEnumScalaSet.contains(ageGroupEnum)
  }

}
