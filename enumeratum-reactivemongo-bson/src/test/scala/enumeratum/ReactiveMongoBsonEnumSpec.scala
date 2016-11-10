package enumeratum

import org.scalatest.{ FunSpec, Matchers }
import reactivemongo.bson._
import org.scalatest.OptionValues._

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
        bsonValue.asOpt[Dummy].value shouldBe Dummy.A
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
