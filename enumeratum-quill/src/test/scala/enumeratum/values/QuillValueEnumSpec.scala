package enumeratum.values

import org.scalatest.{FunSpec, Matchers}

import scala.collection.immutable

class QuillValueEnumSpec extends FunSpec with Matchers {

  describe("An IntQuillEnum") {

    it("should encode to Int") {
      // we only need to test whether it can compile because Quill will fail compilation if an Encoder is not found
      """
        | import io.getquill._
        | val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
        | import ctx._
        | ctx.run(query[QuillBorrowerToLibraryItem].insert(_.borrower -> "Foo", _.item -> lift(QuillLibraryItem.Book: QuillLibraryItem)))
      """.stripMargin should compile
    }

    it("should decode from Int") {
      // we only need to test whether it can compile because Quill will fail compilation if a Decoder is not found
      """
        | import io.getquill._
        | val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
        | import ctx._
        | ctx.run(query[QuillBorrowerToLibraryItem])
      """.stripMargin should compile
    }

  }

}

sealed abstract class QuillLibraryItem(val value: Int, val name: String) extends IntEnumEntry

case object QuillLibraryItem
  extends IntEnum[QuillLibraryItem]
    with IntQuillEnum[QuillLibraryItem] {

  // A good mix of named, unnamed, named + unordered args
  case object Book     extends QuillLibraryItem(value = 1, name = "book")
  case object Movie    extends QuillLibraryItem(name = "movie", value = 2)
  case object Magazine extends QuillLibraryItem(3, "magazine")
  case object CD       extends QuillLibraryItem(4, name = "cd")

  override val values: immutable.IndexedSeq[QuillLibraryItem] = findValues

}

case class QuillBorrowerToLibraryItem(borrower: String, item: QuillLibraryItem)
