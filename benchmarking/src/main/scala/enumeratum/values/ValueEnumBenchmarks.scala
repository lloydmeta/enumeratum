package enumeratum.values

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

import scala.util.control.NonFatal

/** Created by Lloyd on 7/13/16.
  *
  * Copyright 2016
  */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
class ValueEnumBenchmarks {

  @Benchmark
  def withValueExists(bh: Blackhole): Unit = bh.consume {
    Size.withValue(1)
  }

  @Benchmark
  def withValueDoesNotExist(bh: Blackhole): Unit = bh.consume {
    try {
      Size.withValue(10)
    } catch {
      case NonFatal(_) =>
    }
  }

  @Benchmark
  def withValueOptExists(bh: Blackhole): Unit = bh.consume {
    Size.withValueOpt(2)
  }

  @Benchmark
  def withValueOptDoesNotExist(bh: Blackhole): Unit = bh.consume {
    Size.withValueOpt(Int.MaxValue)
  }
}
