package enumeratum

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable

final class QuillEnumSpec extends AnyFunSpec with Matchers {

  describe("A QuillEnum") {

    // we only need to test whether it can compile because Quill will fail compilation if an Encoder is not found
    it("should encode to String") {
      """
import io.getquill._
val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
import ctx._
ctx.run(query[QuillShirt].insert(_.size -> lift(QuillShirtSize.Small: QuillShirtSize)))
      """ should compile
    }

    // we only need to test whether it can compile because Quill will fail compilation if a Decoder is not found
    it("should decode from String") {
      """
import io.getquill._
val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
import ctx._
ctx.run(query[QuillShirt])
      """ should compile
    }

  }

}

sealed trait QuillShirtSize extends EnumEntry

case object QuillShirtSize extends Enum[QuillShirtSize] with QuillEnum[QuillShirtSize] {

  case object Small  extends QuillShirtSize
  case object Medium extends QuillShirtSize
  case object Large  extends QuillShirtSize

  override val values: immutable.IndexedSeq[QuillShirtSize] = findValues

}

case class QuillShirt(size: QuillShirtSize)
