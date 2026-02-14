package test

import enumeratum._

class TestNestedEnum {
  sealed trait Foo extends EnumEntry
  object Foo extends Enum[Foo] {
    lazy val values = findValues
    case object a extends Foo
  }
}
