package enumeratum.values

import org.scalameter.Measurer.BoxingCount
import org.scalameter.api._
import org.scalameter.picklers.noPickler._

class IndexedEnumBoxingBench extends Bench.Forked[Map[String, Long]] {

  import Aggregator.Implicits._

  private val useIndexed = Gen.enumeration("useIndexed")(false, true)

  override def defaultConfig = Context(
    exec.independentSamples -> 1,
    exec.assumeDeterministicRun -> true,
    exec.maxWarmupRuns -> 1,
    exec.minWarmupRuns -> 1,
    exec.benchRuns -> 1
  )
  override def aggregator: Aggregator[Map[String, Long]] = Aggregator.max
  override def measurer = BoxingCount.all()

  performance of "IndexEnum" in {

    measure method "withValueOpt" in {
      using(useIndexed) in { useIndexed =>
        var i = 0
        while (i < 100000) {
          if (useIndexed) {
            Drinks.indexedEnumMap.withValue(1)
            LibraryItem.indexedEnumMap.withValue(1)
          } else {
            Drinks.withValue(1)
            LibraryItem.withValue(1)
          }

          i += 1
        }
      }
    }
  }

}
