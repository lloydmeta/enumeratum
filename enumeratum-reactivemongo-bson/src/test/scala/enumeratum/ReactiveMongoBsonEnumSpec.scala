package enumeratum

import scala.util.Success

import org.scalatest.{FunSpec, Matchers}
import reactivemongo.api.bson.{BSON, BSONDocument, BSONInteger, BSONString, BSONValue}

/**
  *
  * @author Alessandro Lacava (@lambdista)
  * @since 2016-04-23
  */
class ReactiveMongoBsonEnumSpec extends FunSpec with Matchers {

  describe("BSON serdes") {

    describe("deserialisation") {
      it("should work with valid values") {
        val bsonValue: BSONValue = BSONString("A")
        bsonValue.asTry[Dummy] shouldBe Success(Dummy.A)
      }

      it("should work with valid keys") {
        val doc = BSONDocument("1" -> "foo", "2" -> "bar")
        val map = Map(1            -> "foo", 2   -> "bar")

        doc.asTry[Map[Int, String]] shouldBe Success(map)
        BSON.writeDocument(map) shouldBe Success(doc)
      }

      it("should fail with invalid values") {
        val strBsonValue: BSONValue = BSONString("D")
        val intBsonValue: BSONValue = BSONInteger(2)

        strBsonValue.asOpt[Dummy] shouldBe None
        intBsonValue.asOpt[Dummy] shouldBe None
      }
    }
  }

}
