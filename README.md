# Enumeratum [![Build Status](https://travis-ci.org/lloydmeta/enumeratum.svg)](https://travis-ci.org/lloydmeta/enumeratum) [![Coverage Status](https://coveralls.io/repos/lloydmeta/enumeratum/badge.png)](https://coveralls.io/r/lloydmeta/enumeratum)

Yet another enumeration implementation for Scala for the sake of exhaustive pattern match warnings, Enumeratum is
an implementation based on a single Scala macro that searches for implementations of a sealed trait.

Compatible with Scala 2.10.x and 2.11.x

# SBT

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum" % "0.0.3", 
)
```

## Example

Using Enumeratum is simple. Simply declare your own sealed trait `A`, and implement it in case objects inside
an object that extends from `Enum[A]` as follows.

*Note* `Enum` is BYOO (Bring Your Own Ordinality) - take care of ordinality in your own way when you 
implement the value method. If you don't care about ordinality, just pass `findValues` directly into your
`val values` implementation.

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

## Licence

The MIT License (MIT)

Copyright (c) 2014 by Lloyd Chan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.