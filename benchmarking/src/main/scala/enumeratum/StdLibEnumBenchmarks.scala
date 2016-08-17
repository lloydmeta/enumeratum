package enumeratum

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole

import scala.util.control.NonFatal

/**
 * Created by Lloyd on 8/18/16.
 *
 * Copyright 2016
 */

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
class StdLibEnumBenchmarks {

  @Benchmark
  def withNameExists(bh: Blackhole): Unit = bh.consume {
    Workday.withName("Monday")
  }

  @Benchmark
  def withNameDoesNotExist(bh: Blackhole): Unit = bh.consume {
    try {
      Workday.withName("Sunday")
    } catch {
      case NonFatal(_) =>
    }
  }

}