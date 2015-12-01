package enumeratum

import org.scalatest.{ FunSpec, Matchers }

class EnumSetSpec extends FunSpec with Matchers {

  describe("mutable") {
    describe("empty") {
      val emptySet = new MutableEnumSet(DummyEnum)
      it("should have size = 0") {
        emptySet shouldBe 'empty
      }

      it("should have an empty iterator") {
        emptySet.iterator shouldBe 'empty
      }

      it("should not contain anything") {
        DummyEnum.values.foreach {
          de => emptySet.contains(de) shouldBe false
        }
      }
    }

    describe("mutations") {
      val set = new MutableEnumSet(DummyEnum)
      it("should grow after being added an item") {
        set += DummyEnum.Hello
        set.size shouldBe 1
        set.iterator.toSeq shouldBe Seq(DummyEnum.Hello)
        set.contains(DummyEnum.Hello) shouldBe true
      }

      it("should not grow again after being added the same item") {
        set += DummyEnum.Hello
        set.size shouldBe 1
        set.iterator.toSeq shouldBe Seq(DummyEnum.Hello)
        set.contains(DummyEnum.Hello) shouldBe true
      }

      it("should not shrink after being removed a non-existing key") {
        set -= DummyEnum.Hi
        set.size shouldBe 1
        set.iterator.toSeq shouldBe Seq(DummyEnum.Hello)
        set.contains(DummyEnum.Hello) shouldBe true
      }

      it("should shrink after being removed a key") {
        set -= DummyEnum.Hello
        set.size shouldBe 0
        set.iterator.toSeq shouldBe Nil
        set.contains(DummyEnum.Hello) shouldBe false
      }
    }
  }

  describe("immutable") {
    describe("empty") {
      val emptySet = new ImmutableEnumSet(DummyEnum)()
      it("should have size = 0") {
        emptySet shouldBe 'empty
      }

      it("should have an empty iterator") {
        emptySet.iterator shouldBe 'empty
      }

      it("should not contain anything") {
        DummyEnum.values.foreach {
          de => emptySet.contains(de) shouldBe false
        }
      }
    }

    describe("mutations") {
      it("should grow after being added an entry") {
        val set = new ImmutableEnumSet(DummyEnum)()
        val biggerSet = set + DummyEnum.Hello
        biggerSet.size shouldBe 1
        biggerSet.iterator.toSeq shouldBe Seq(DummyEnum.Hello)
        biggerSet.contains(DummyEnum.Hello) shouldBe true
      }

      it("should not grow again after being added another entry with the same key") {
        val set = new ImmutableEnumSet(DummyEnum)(DummyEnum.Hello)
        val biggerSet = set + DummyEnum.Hello
        biggerSet.size shouldBe 1
        biggerSet.iterator.toSeq shouldBe Seq(DummyEnum.Hello)
        biggerSet.contains(DummyEnum.Hello) shouldBe true
      }

      it("should not shrink after being removed a non-existing key") {
        val set = new ImmutableEnumSet(DummyEnum)(DummyEnum.Hello)
        val biggerSet = set - DummyEnum.Hi
        biggerSet.size shouldBe 1
        biggerSet.iterator.toSeq shouldBe Seq(DummyEnum.Hello)
        biggerSet.contains(DummyEnum.Hello) shouldBe true
      }

      it("should shrink after being removed a key") {
        val set = new ImmutableEnumSet(DummyEnum)(DummyEnum.Hello)
        val biggerSet = set - DummyEnum.Hello
        biggerSet.size shouldBe 0
        biggerSet.iterator.toSeq shouldBe Nil
        biggerSet.contains(DummyEnum.Hello) shouldBe false
      }
    }
  }
}
