package enumeratum

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
class EnumBenchmarks {

  @Benchmark
  def withNameExists(bh: Blackhole): Unit = bh.consume {
    AgeGroup.withName("Baby")
  }

  @Benchmark
  def indexOf(bh: Blackhole): Unit = bh.consume {
    AgeGroup.indexOf(AgeGroup.Baby)
  }

  @Benchmark
  def withNameDoesNotExist(bh: Blackhole): Unit = bh.consume {
    try {
      AgeGroup.withName("Alien")
    } catch {
      case NonFatal(_) =>
    }
  }

  @Benchmark
  def withNameOptionExists(bh: Blackhole): Unit = bh.consume {
    AgeGroup.withNameOption("Adult")
  }

  @Benchmark
  def withNameOptionDoesNotExist(bh: Blackhole): Unit = bh.consume {
    AgeGroup.withNameOption("Pooper")
  }

  @Benchmark
  def entryNameStandard(bh: Blackhole): Unit = bh.consume {
    AgeGroup.Adult.entryName
  }

  @Benchmark
  def entryNameStacked(bh: Blackhole): Unit = bh.consume {
    StackedEnum.SomethingBlueAndBig.entryName
  }

}
