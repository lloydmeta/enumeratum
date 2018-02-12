package enumeratum

import org.scalatest.{FunSpec, Matchers}

import scala.collection.immutable

class QuillEnumSpec extends FunSpec with Matchers {

  describe("to SQL String") {

    // we only need to test whether it can compile because Quill will fail compilation if an Encoder is not found
    it("should compile") {
      """
        | import io.getquill._
        | val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
        | import ctx._
        | ctx.run(query[Shirt].insert(_.size -> lift(ShirtSize.Small: ShirtSize)))
      """.stripMargin should compile
    }

  }

  describe("from SQL String") {

    // we only need to test whether it can compile because Quill will fail compilation if a Decoder is not found
    it("should compile") {
      """
        | import io.getquill._
        | val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
        | import ctx._
        | ctx.run(query[Shirt])
      """.stripMargin should compile
    }

  }
}

sealed trait ShirtSize extends EnumEntry

case object ShirtSize extends Enum[ShirtSize] with QuillEnum[ShirtSize] {

  case object Small  extends ShirtSize
  case object Medium extends ShirtSize
  case object Large  extends ShirtSize

  override val values: immutable.IndexedSeq[ShirtSize] = findValues

}

case class Shirt(size: ShirtSize)
