# Enumeratum

Yet another enumeration implementation for Scala for the sake of exhaustive pattern match warnings, Enumeratum is
an implementation based on a single Scala macro that searches for implementations of a sealed trait.

Compatible with Scala 2.10.x and 2.11.x

## Example

Using Enumeratum is simple. Simply declare your own sealed trait, and implement as follows.

*Note* `Enum` is BYOO (bring your own ordinality). Take care of that in your own way when you implement the
value method. If you don't care about ordinality, just pass `findValues` directly into values.

```scala

import enumeratum.Enum

sealed trait MyEnum

object MyEnum extends Enum[MyEnum] {

  /* Implement the values method as a val */
  val values = findValues

  case object Hello extends MyEnum
  case object GoodBye extends MyEnum
  case object Hi extends MyEnum

}

```