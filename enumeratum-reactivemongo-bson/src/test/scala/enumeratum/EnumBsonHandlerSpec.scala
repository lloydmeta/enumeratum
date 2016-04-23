package enumeratum

import org.scalatest.OptionValues._
import org.scalatest.{ FunSpec, Matchers }
import reactivemongo.bson._

/**
 *
 * @author Alessandro Lacava (@lambdista)
 * @since 2016-04-23
 */
class EnumBsonHandlerSpec extends FunSpec with Matchers {

  describe("reader") {
    val reader = EnumHandler.reader(Dummy)

    it("should create a reader that works with valid values") {
      reader.readOpt(BSONString("A")).value should be(Dummy.A)
    }

    it("should create a reader that fails with invalid values") {
      reader.readOpt(BSONString("D")).isEmpty should be(true)
      reader.readOpt(BSONInteger(2)).isEmpty should be(true)
    }
  }

  describe("reader insensitive") {
    val reader = EnumHandler.reader(Dummy, true)

    it("should create a reader that works with valid values disregarding case") {
      reader.readOpt(BSONString("A")).value should be(Dummy.A)
      reader.readOpt(BSONString("a")).value should be(Dummy.A)
    }

    it("should create a reader that fails with invalid values") {
      reader.readOpt(BSONString("D")).isEmpty should be(true)
      reader.readOpt(BSONInteger(2)).isEmpty should be(true)
    }
  }

  describe("writes") {
    val writer = EnumHandler.writer(Dummy)

    it("should create a writer that writes enum values to BSONString") {
      writer.write(Dummy.A) should be(BSONString("A"))
    }
  }

  describe("formats") {
    val handler = EnumHandler.handler(Dummy)

    it("should create a format that works with valid values") {
      handler.readOpt(BSONString("A")).value should be(Dummy.A)
    }

    it("should create a format that fails with invalid values") {
      handler.readOpt(BSONString("D")).isEmpty should be(true)
      handler.readOpt(BSONInteger(2)).isEmpty should be(true)
    }

    it("should create a format that writes enum values to BSONString") {
      handler.write(Dummy.A) should be(BSONString("A"))
    }
  }

}
