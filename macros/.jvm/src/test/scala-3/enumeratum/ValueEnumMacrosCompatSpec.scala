package enumeratum

import java.nio.file.{Files, Paths}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Regression test for #477: the value-enum macro must not depend on compiler-internal APIs.
  *
  * `scala.quoted.runtime.impl.QuotesImpl`, `dotty.tools.dotc.core.Contexts.Context` and
  * `ScalaSettings` have no binary compatibility guarantees and changed in recent Scala 3
  * releases, which makes `findValues` crash with a `NoSuchMethodError` in downstream projects.
  * A check of `-Yretain-trees` may only rely on public APIs (e.g. the availability of
  * definition trees).
  */
class ValueEnumMacrosCompatSpec extends AnyFunSpec with Matchers {

  private val macroSource: String = {
    val candidates = Seq(
      Paths.get("src", "main", "scala-3", "enumeratum", "ValueEnumMacros.scala"),
      Paths.get("macros", "src", "main", "scala-3", "enumeratum", "ValueEnumMacros.scala")
    )
    val path = candidates.find(Files.isRegularFile(_)).getOrElse(
      throw new IllegalStateException(
        s"Could not locate ValueEnumMacros.scala from ${candidates.mkString(", ")}"
      )
    )
    new String(Files.readAllBytes(path), "UTF-8")
  }

  describe("ValueEnumMacros Scala 3 implementation") {
    it("does not reference compiler-internal APIs") {
      val forbidden = Seq(
        "scala.quoted.runtime.impl.QuotesImpl",
        "dotty.tools.dotc"
      )
      forbidden.foreach { api =>
        withClue(s"macro source must not reference compiler internals ($api):") {
          macroSource should not include api
        }
      }
    }

    it("does not read the YretainTrees setting directly") {
      withClue("macro source must determine tree availability via public APIs:") {
        macroSource should not include "YretainTrees"
      }
    }

    it("expands value enums using only public APIs") {
      // Compiled with -Yretain-trees (see Test / scalacOptions in build.sbt). If the macro
      // still cast Quotes to a compiler-internal class, this expansion would fail to link.
      val found = FindValEnums[CompatColor]
      found.map(_.value) should contain theSameElementsAs Seq(1, 2, 3)
    }
  }
}

/** Minimal value enum used to exercise the macro at compile time. */
sealed abstract class CompatColor(val value: Int)
object CompatColor {
  case object Red   extends CompatColor(1)
  case object Green extends CompatColor(2)
  case object Blue  extends CompatColor(3)
}
