package enumeratum

import org.scalatest.{FunSpec, Matchers}

class EnumJVMSpec extends FunSpec with Matchers {

  describe("findValues Vector") {

    // This is a fairly intense test.
    it("should be in the same order that the objects were declared in") {
      import scala.util._
      (1 to 100).foreach { i =>
        val members = Random.shuffle((1 to Random.nextInt(20)).map { m =>
          s"Member$m"
        })
        val membersDefs = members.map { m =>
          s"case object $m extends Enum$i"
        }.mkString("\n\n")
        val objDefinition =
          s"""
              import enumeratum._
              sealed trait Enum$i extends EnumEntry

              case object Enum$i extends Enum[Enum$i] {
               $membersDefs
               val values = findValues
              }

              Enum$i
             """
        val obj = Eval.apply[Enum[_ <: EnumEntry]](objDefinition)
        obj.values.map(_.entryName) shouldBe members
      }
    }

  }

}
