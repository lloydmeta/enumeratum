package enumeratum

import org.scalatest.{ Matchers, FunSpec }

class EnumMapSpec extends FunSpec with Matchers {

  describe("mutable") {
    describe("empty") {
      val emptyMap = new MutableEnumMap[DummyEnum, Int](DummyEnum)
      it("should have size = 0") {
        emptyMap shouldBe 'empty
      }

      it("should have an empty iterator") {
        emptyMap.iterator shouldBe 'empty
      }

      it("should not contain anything") {
        DummyEnum.values.foreach {
          de => emptyMap.get(de) shouldBe None
        }
      }
    }

    describe("mutations") {
      val map = new MutableEnumMap[DummyEnum, Int](DummyEnum)
      it("should grow after being added an entry") {
        map += DummyEnum.Hello -> 42
        map.size shouldBe 1
        map.iterator.toSeq shouldBe Seq(DummyEnum.Hello -> 42)
        map.get(DummyEnum.Hello) shouldBe Some(42)
      }

      it("should not grow again after being added another entry with the same key") {
        map += DummyEnum.Hello -> 43
        map.size shouldBe 1
        map.get(DummyEnum.Hello) shouldBe Some(43)
      }

      it("should not shrink after being removed a non-existing key") {
        map -= DummyEnum.Hi
        map.size shouldBe 1
        map.iterator.toSeq shouldBe Seq(DummyEnum.Hello -> 43)
      }

      it("should shrink after being removed a key") {
        map -= DummyEnum.Hello
        map.size shouldBe 0
        map.iterator.toSeq shouldBe Nil
      }
    }
  }

  describe("immutable") {
    describe("empty") {
      val emptyMap = new ImmutableEnumMap[DummyEnum, Int](DummyEnum)()
      it("should have size = 0") {
        emptyMap shouldBe 'empty
      }

      it("should have an empty iterator") {
        emptyMap.iterator shouldBe 'empty
      }

      it("should not contain anything") {
        DummyEnum.values.foreach {
          de => emptyMap.get(de) shouldBe None
        }
      }
    }

    describe("mutations") {
      it("should grow after being added an entry") {
        val map = new ImmutableEnumMap[DummyEnum, Int](DummyEnum)()
        val biggerMap = map + (DummyEnum.Hello -> 42)
        biggerMap.size shouldBe 1
        biggerMap.iterator.toSeq shouldBe Seq(DummyEnum.Hello -> 42)
        biggerMap.get(DummyEnum.Hello) shouldBe Some(42)
      }

      it("should not grow again after being added another entry with the same key") {
        val map = new ImmutableEnumMap[DummyEnum, Int](DummyEnum)(DummyEnum.Hello -> 42)
        val biggerMap = map + (DummyEnum.Hello -> 43)
        biggerMap.size shouldBe 1
        biggerMap.get(DummyEnum.Hello) shouldBe Some(43)
      }

      it("should not shrink after being removed a non-existing key") {
        val map = new ImmutableEnumMap[DummyEnum, Int](DummyEnum)(DummyEnum.Hello -> 42)
        val biggerMap = map - DummyEnum.Hi
        biggerMap.size shouldBe 1
        biggerMap.iterator.toSeq shouldBe Seq(DummyEnum.Hello -> 42)
      }

      it("should shrink after being removed a key") {
        val map = new ImmutableEnumMap[DummyEnum, Int](DummyEnum)(DummyEnum.Hello -> 42)
        val biggerMap = map - DummyEnum.Hello
        biggerMap.size shouldBe 0
        biggerMap.iterator.toSeq shouldBe Nil
      }
    }
  }
}
