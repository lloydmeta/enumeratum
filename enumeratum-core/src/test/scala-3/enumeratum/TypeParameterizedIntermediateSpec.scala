package enumeratum

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TypeParameterizedIntermediateSpec extends AnyFunSpec with Matchers {

  describe("Type-parametrized enums with intermediate hierarchies") {
    it("should find all values when intermediate trait has type parameters") {
      sealed trait Foo2[T] extends EnumEntry with Serializable

      object Foo2 extends Enum[Foo2[Unit]] {
        sealed trait Bar[T] extends Foo2[T]

        case object A extends Bar[Unit]
        case object B extends Foo2[Unit]
        lazy val values: IndexedSeq[Foo2[Unit]] = findValues
      }

      Foo2.values should contain theSameElementsAs Seq(Foo2.A, Foo2.B)
    }

    it("should exclude case objects with different type parameters") {
      sealed trait TypedEnum[T] extends EnumEntry

      object TypedEnum {
        sealed trait IntermediateTrait[T] extends TypedEnum[T]
      }

      object UnitEnum extends Enum[TypedEnum[Unit]] {
        lazy val values: IndexedSeq[TypedEnum[Unit]] = findValues

        case object UnitValue1 extends TypedEnum[Unit]
        case object UnitValue2 extends TypedEnum.IntermediateTrait[Unit]
        // These should NOT be included - different type parameter
        case object IntValue    extends TypedEnum[Int]
        case object StringValue extends TypedEnum.IntermediateTrait[String]
      }

      // Should only find Unit-typed values
      UnitEnum.values should contain theSameElementsAs Seq(
        UnitEnum.UnitValue1,
        UnitEnum.UnitValue2
      )
      UnitEnum.values should not contain UnitEnum.IntValue
      UnitEnum.values should not contain UnitEnum.StringValue
    }

    it("should find all values in complex hierarchy with type parameters") {
      sealed trait Account[A] extends EnumEntry
      object Account {
        sealed trait Asset[A]     extends Account[A]
        sealed trait Liability[A] extends Account[A]
      }

      case class User()
      case class Company()

      object UserAccounts extends Enum[Account[User]] {
        lazy val values: IndexedSeq[Account[User]] = findValues

        case object Savings    extends Account.Asset[User]
        case object Checking   extends Account.Asset[User]
        case object CreditCard extends Account.Liability[User]
      }

      UserAccounts.values should contain theSameElementsAs Seq(
        UserAccounts.Savings,
        UserAccounts.Checking,
        UserAccounts.CreditCard
      )
    }
  }
}
