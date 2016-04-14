# Enumeratum [![Build Status](https://travis-ci.org/lloydmeta/enumeratum.svg?branch=master)](https://travis-ci.org/lloydmeta/enumeratum) [![Coverage Status](https://coveralls.io/repos/lloydmeta/enumeratum/badge.svg?branch=master)](https://coveralls.io/r/lloydmeta/enumeratum?branch=master) [![Codacy Badge](https://www.codacy.com/project/badge/a71a20d8678f4ed3a5b74b0659c1bc4c)](https://www.codacy.com/public/lloydmeta/enumeratum)

Enumeratum is a type-safe and powerful enumeration implementation for Scala that offers exhaustive pattern match warnings,
integrations with popular Scala libraries, and idiomatic usage that won't break your IDE.

Enumeratum aims to be similar enough to Scala's built in `Enumeration` to be easy-to-use and understand while offering
more flexibility, safety, and power. It also has **zero** dependencies, which means it's light-weight, but more importantly,
won't clutter your (or your dependants') namespace.

Using Enumeratum allows you to use your own `sealed` traits/classes without having to maintain your own collection of
values, which not only means you get exhaustive pattern match warnings, but also richer enum values, and methods that
can take your enum values as arguments without having to worry about erasure (for more info, see [this blog post on Scala's
`Enumeration`](http://underscore.io/blog/posts/2014/09/03/enumerations.html))


Enumeratum has the following niceties:

- Zero dependencies
- Allows your Enum members to be full-fledged normal objects with methods, values, inheritance, etc.
- Simplicity; most of the complexity in this lib is in its macro, and the macro is fairly simple conceptually
- As idiomatic as possible: you're very clearly still writing Scala, and no funny colours in your IDE means less cognitive overhead for your team
- No usage of `synchronized`, which may help with performance and deadlocks prevention
- No usage of reflection at run time. This may also help with performance but it means Enumeratum is compatible with ScalaJS and other
  environments where reflection is a best effort.
- All magic happens at compile-time so you know right away when things go awry

Compatible with Scala 2.11+ and 2.10.

[Scaladocs](https://beachape.com/enumeratum/latest/api)

### Table of Contents

1. [Quick start](#quick-start)
  1. [SBT](#sbt)
  2. [Usage](#usage)
2. [More examples](#more-examples)
  1. [Enum](#enum)
    1. [Mixins](#mixins)
  2. [ValueEnum](#valueenum)
2. [ScalaJS](#scalajs)
3. [Play integration](#play-integration)
4. [Play JSON integration](#play-json)
5. [Circe integration](#circe)
6. [UPickle integration](#upickle)
7. [Scala 2.10](#scala-210)
8. [Licence](#licence)


## Quick start

### SBT

In `build.sbt`, set the Enumeratum version in a variable (for the latest version, use `val enumeratumVersion = "1.3.7"`).

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum" % enumeratumVersion
)
```

Enumeratum has different integrations that can be added to your build a la cart. For more info, see the respective secions in
[the Table of Contents](#table-of-contents)

### Usage

Using Enumeratum is simple. Just declare your own sealed trait or class `A` that extends `EnumEntry` and implement it as case objects inside
an object that extends from `Enum[A]` as shown below.

```scala

import enumeratum._

sealed trait Greeting extends EnumEntry

object Greeting extends Enum[Greeting] {

  /*
   `findValues` is a protected method that invokes a macro to find all `Greeting` object declarations inside an `Enum`

   You use it to implement the `val values` member
  */
  val values = findValues

  case object Hello extends Greeting
  case object GoodBye extends Greeting
  case object Hi extends Greeting
  case object Bye extends Greeting

}

// Object Greeting has a `withName(name: String)` method
Greeting.withName("Hello")

// => res0: Greeting = Hello

Greeting.withName("Haro")
// => java.lang.IllegalArgumentException: Haro is not a member of Enum (Hello, GoodBye, Hi, Bye)

```

Note that by default, `findValues` will return a `Seq` with the enum members listed in written-order (relevant if you want to
use the `indexOf` method).


## More examples

### Enum

Continuing from the enum declared in [the quick-start section](#usage):

```scala
import Greeting._

def tryMatching(v: Greeting): Unit = v match {
  case Hello => println("Hello")
  case GoodBye => println("GoodBye")
  case Hi => println("Hi")
}

/**
Pattern match warning ...

<console>:24: warning: match may not be exhaustive.
It would fail on the following input: Bye
       def tryMatching(v: Greeting): Unit = v match {

*/

Greeting.indexOf(Bye)
// => res2: Int = 3

```

The name is taken from the `toString` method of the particular
`EnumEntry`. This behavior can be changed in two ways. The first is
to manually override the `def entryName: String` method.

```scala

import enumeratum._

sealed abstract class State(override def entryName: String) extends EnumEntry

object State extends Enum[State] {

   val values = findValues

   case object Alabama extends State("AL")
   case object Alaska extends State("AK")
   // and so on and so forth.
}

import State._

State.withName("AL")

```

### Mixins

The second is to mixin the stackable traits provided for common string
conversions, `Snakecase`, `Uppercase`, and `Lowercase`.

```scala

import enumeratum._
import enumeratum.EnumEntry._

sealed trait Greeting extends EnumEntry with Snakecase

object Greeting extends Enum[Greeting] {

  val values = findValues

  case object Hello extends Greeting
  case object GoodBye extends Greeting
  case object ShoutGoodBye extends Greeting with Uppercase

}

Greeting.withName("hello")
Greeting.withName("good_bye")
Greeting.withName("SHOUT_GOOD_BYE")

```

### ValueEnum

Asides from enumerations that resolve members from `String` names, Enumeratum also supports `ValueEnum`s, enums that resolve
members from various primitive types like `Int`, `Long` and`Short`. In order to ensure at compile-time that multiple members
do not share the same value, these enums are powered by a separate macro and exposed through a different set of traits.

```scala
import enumeratum.values._

sealed abstract class LibraryItem(val value: Int, val name: String) extends IntEnumEntry

case object LibraryItem extends IntEnum[LibraryItem] {

  case object Book extends LibraryItem(value = 1, name = "book")
  case object Movie extends LibraryItem(name = "movie", value = 2)
  case object Magazine extends LibraryItem(3, "magazine")
  case object CD extends LibraryItem(4, name = "cd")
  // case object Newspaper extends LibraryItem(4, name = "cd") <-- will fail to compile because the value 4 is shared

  /*
  val five = 5
  case object Article extends LibraryItem(five, name = "five") <-- will fail to compile because the value is not a literal
  */

  val values = findValues

}
```

** Restrictions **
- `ValueEnum`s must have their value members implemented as literal values.
- The macro behind this enum does not work within the REPL, but works in normally compiled code.
- `ValueEnums` are not available for Scala 2.10 projects.


## ScalaJS

In a ScalaJS project, add the following to `build.sbt`:

```scala
libraryDependencies ++= Seq(
    "com.beachape" %%% "enumeratum" % enumeratumVersion
)
```

As expected, usage is exactly the same as normal Scala.

## Play Integration

The `enumeratum-play` project is published separately and gives you access to various tools
to help you avoid boilerplate in your Play project.

### SBT

For enumeratum with full Play support:
```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum" % enumeratumVersion,
    "com.beachape" %% "enumeratum-play" % enumeratumVersion
)
```

### Usage


The included `PlayEnum` trait is probably going to be the most interesting as it includes a bunch
of built-in implicits like Json formats, Path bindables, Query string bindables,
and form field support.

For example:

```scala
package enums._

import enumeratum._

sealed trait Greeting extends EnumEntry

object Greeting extends PlayEnum[Greeting] {

  val values = findValues

  case object Hello extends Greeting
  case object GoodBye extends Greeting
  case object Hi extends Greeting
  case object Bye extends Greeting

}

/*
  Then make sure to import your PlayEnums into your routes in your Build.scala
  or build.sbt so that you can use them in your routes file.

  `routesImport += "enums._"`
*/


// You can also use the String Interpolating Routing DSL:

import play.api.routing.sird._
import play.api.routing._
import play.api.mvc._
Router.from {
    case GET(p"/hello/${Greeting.fromPath(greeting)}") => Action {
      Results.Ok(s"$greeting")
    }
}

```

### PlayValueEnums

There are `IntPlayEnum`, `LongPlayEnum`, and `ShortPlayEnum` traits for use with `IntEnumEntry`, `LongEnumEntry`, and
`ShortEnumEntry` respectively that provide Play-specific implicits as with normal `PlayEnum`. For example:

```scala
import enumeratum.values._

sealed abstract class PlayLibraryItem(val value: Int, val name: String) extends IntEnumEntry

case object PlayLibraryItem extends IntPlayEnum[PlayLibraryItem] {

  // A good mix of named, unnamed, named + unordered args
  case object Book extends PlayLibraryItem(value = 1, name = "book")
  case object Movie extends PlayLibraryItem(name = "movie", value = 2)
  case object Magazine extends PlayLibraryItem(3, "magazine")
  case object CD extends PlayLibraryItem(4, name = "cd")

  val values = findValues

}

import play.api.libs.json.{ JsNumber, JsString, Json => PlayJson }
assert(PlayJson.toJson(PlayLibraryItem.Book) == JsNumber(1))
```


## Play JSON

The `enumeratum-play-json` project is published separately and gives you access to Play's auto-generated boilerplate
for JSON serialization in your Enum's.

### SBT

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum" % enumeratumVersion,
    "com.beachape" %% "enumeratum-play-json" % enumeratumVersion
)
```

### Usage

For example:

```scala
import enumeratum.{ PlayJsonEnum, Enum, EnumEntry }

sealed trait Greeting extends EnumEntry

object Greeting extends Enum[Greeting] with PlayJsonEnum[Greeting] {

  val values = findValues

  case object Hello extends Greeting
  case object GoodBye extends Greeting
  case object Hi extends Greeting
  case object Bye extends Greeting

}

```

### PlayJsonValueEnum

There are `IntPlayJsonEnum`, `LongPlayJsonEnum`, and `ShortPlayJsonEnum` traits for use with `IntEnumEntry`, `LongEnumEntry`, and
`ShortEnumEntry` respectively. For example:

```scala
import enumeratum.values._

sealed abstract class JsonDrinks(val value: Short, name: String) extends ShortEnumEntry

case object JsonDrinks extends ShortEnum[JsonDrinks] with ShortPlayJsonValueEnum[JsonDrinks] {

  case object OrangeJuice extends JsonDrinks(value = 1, name = "oj")
  case object AppleJuice extends JsonDrinks(value = 2, name = "aj")
  case object Cola extends JsonDrinks(value = 3, name = "cola")
  case object Beer extends JsonDrinks(value = 4, name = "beer")

  val values = findValues

}

import play.api.libs.json.{ JsNumber, JsString, Json => PlayJson, JsSuccess }

// Use to deserialise numbers to enum members directly
JsonDrinks.values.foreach { drink =>
    assert(PlayJson.toJson(drink) == JsNumber(drink.value))
}
assert(PlayJson.fromJson[JsonDrinks](JsNumber(3)) == JsSuccess(JsonDrinks.Cola))
assert(PlayJson.fromJson[JsonDrinks](JsNumber(19)).isError)
```

## Circe

### SBT

To use enumeratum with [Circe](https://github.com/travisbrown/circe):

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum" % enumeratumVersion,
    "com.beachape" %% "enumeratum-circe" % enumeratumVersion
)
```

To use with ScalaJS:

```scala
libraryDependencies ++= Seq(
    "com.beachape" %%% "enumeratum" % enumeratumVersion,
    "com.beachape" %%% "enumeratum-circe" % enumeratumVersion
)
```

### Usage

#### Enum

```scala
import enumeratum._

sealed trait ShirtSize extends EnumEntry

case object ShirtSize extends CirceEnum[ShirtSize] with Enum[ShirtSize] {

  case object Small extends ShirtSize
  case object Medium extends ShirtSize
  case object Large extends ShirtSize

  val values = findValues

}

import io.circe.Json
import io.circe.syntax._

ShirtSize.values.foreach { size =>
    assert(size.asJson == Json.fromString(size.entryName))
}

```

#### ValueEnum

```scala
import enumeratum.values._

sealed abstract class CirceLibraryItem(val value: Int, val name: String) extends IntEnumEntry

case object CirceLibraryItem extends IntEnum[CirceLibraryItem] with IntCirceEnum[CirceLibraryItem] {

  // A good mix of named, unnamed, named + unordered args
  case object Book extends CirceLibraryItem(value = 1, name = "book")
  case object Movie extends CirceLibraryItem(name = "movie", value = 2)
  case object Magazine extends CirceLibraryItem(3, "magazine")
  case object CD extends CirceLibraryItem(4, name = "cd")

  val values = findValues

}

import io.circe.Json
import io.circe.syntax._

CirceLibraryItem.values.foreach { item =>
    assert(item.asJson == Json.fromInt(item.value))
}
```

## UPickle

### SBT

To use enumeratum with [uPickle](http://lihaoyi.github.io/upickle/):

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum" % enumeratumVersion,
    "com.beachape" %% "enumeratum-upickle" % enumeratumVersion
)
```

To use with ScalaJS:

```scala
libraryDependencies ++= Seq(
    "com.beachape" %%% "enumeratum" % enumeratumVersion,
    "com.beachape" %%% "enumeratum-upickle" % enumeratumVersion
)
```

### Usage

`CirceEnum` works pretty much the same as `CirceEnum` and `PlayJsonEnum` variants, so we'll skip straight to the 
`ValueEnum` integration.

```scala
import enumeratum.values._

sealed abstract class ContentType(val value: Long, name: String) extends LongEnumEntry

case object ContentType
    extends LongEnum[ContentType]
    with LongUPickleEnum[ContentType] {

  val values = findValues

  case object Text extends ContentType(value = 1L, name = "text")
  case object Image extends ContentType(value = 2L, name = "image")
  case object Video extends ContentType(value = 3L, name = "video")
  case object Audio extends ContentType(value = 4L, name = "audio")

}

import upickle.default.{ readJs, writeJs, Reader, Writer }
enum.values.foreach { entry =>
  val written = writeJs(entry)
  assert(readJs(written) == entry)
}

```

## Scala 2.10

Scala's Macro API is experimental and has changed quite a bit between 2.10 and 2.11, so some features of Enumeratum are
not available in 2.10 (though PRs making them available are welcome):

- [Value Enums](#valueenum): The `.tpe` of constructor functions are not resolved yet, so we can't resolve `value` arguments

## Licence

The MIT License (MIT)

Copyright (c) 2016 by Lloyd Chan

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
