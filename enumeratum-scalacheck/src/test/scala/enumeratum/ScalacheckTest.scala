package enumeratum

import org.scalacheck.{Arbitrary, Cogen}
import org.scalacheck.rng.Seed
import org.scalatest.{FunSpecLike, Matchers}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

trait ScalacheckTest {
  self: FunSpecLike with ScalaCheckDrivenPropertyChecks with Matchers =>

  private implicit val arbSeed: Arbitrary[Seed] = Arbitrary(
    Arbitrary.arbitrary[Long].map(Seed.apply))

  def test[BaseType, EnumType <: BaseType: Arbitrary: Cogen](label: String): Unit =
    describe(s"Cogen[$label]") {

      // Cogen must follow the four laws defined in Scalacheck's CogenSpecification, but they are
      // not published, so test that we did not break them. Incidentally, this also tests Arbitrary.

      it("should be consistent") {
        forAll { (seed: Seed, value: EnumType) =>
          Cogen[EnumType].perturb(seed, value) should be(Cogen[EnumType].perturb(seed, value))
        }
      }

      it("should be injective") {
        forAll { (seed: Seed, one: EnumType, other: EnumType) =>
          whenever(one != other) {
            Cogen[EnumType].perturb(seed, one) should not be Cogen[EnumType].perturb(seed, other)
          }
        }
      }

      it("should preserve identity") {
        forAll { (seed: Seed, value: EnumType) =>
          Cogen[EnumType].contramap(identity[EnumType]).perturb(seed, value) should be(
            Cogen[EnumType].perturb(seed, value))
        }
      }

      it("should preserve composition") {
        forAll { (seed: Seed, value: Int, f: Int => EnumType, g: Int => Int) =>
          Cogen[EnumType].contramap(f).contramap(g).perturb(seed, value) should be(
            Cogen[EnumType].contramap(f compose g).perturb(seed, value))
        }
      }

    }

}
