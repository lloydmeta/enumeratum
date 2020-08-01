# Enumeratum [![Build Status](https://travis-ci.org/lloydmeta/enumeratum.svg?branch=master)](https://travis-ci.org/lloydmeta/enumeratum) [![Coverage Status](https://coveralls.io/repos/lloydmeta/enumeratum/badge.svg?branch=master)](https://coveralls.io/r/lloydmeta/enumeratum?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/a71a20d8678f4ed3a5b74b0659c1bc4c)](https://www.codacy.com/app/lloydmeta/enumeratum?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lloydmeta/enumeratum&amp;utm_campaign=Badge_Grade) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum_2.11) [![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.0.svg)](https://www.scala-js.org) [![Join the chat at https://gitter.im/lloydmeta/enumeratum](https://badges.gitter.im/lloydmeta/enumeratum.svg)](https://gitter.im/lloydmeta/enumeratum?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


Enumeratum is a type-safe and powerful enumeration implementation for Scala that offers exhaustive pattern match warnings,
integrations with popular Scala libraries, and idiomatic usage that won't break your IDE. It aims to be similar enough
to Scala's built in `Enumeration` to be easy-to-use and understand while offering more flexibility, type-safety (see [this blog
post describing erasure on Scala's `Enumeration`](http://underscore.io/blog/posts/2014/09/03/enumerations.html)), and
richer enum values without having to maintain your own collection of values.

Enumeratum has the following niceties:

- Zero dependencies
- Performant: Faster than`Enumeration` in the standard library (see [benchmarks](#benchmarking))
- Allows your Enum members to be full-fledged normal objects with methods, values, inheritance, etc.
- [`ValueEnum`s](#valueenum) that map to various primitive values and have compile-time uniqueness constraints.
- Idiomatic: you're very clearly still writing Scala, and no funny colours in your IDE means less cognitive overhead for your team
- Simplicity; most of the complexity in this lib is in its macro, and the macro is fairly simple conceptually
- No usage of reflection at runtime. This may also help with performance but it means Enumeratum is compatible with ScalaJS and other
  environments where reflection is a best effort (such as Android)
- No usage of `synchronized`, which may help with performance and deadlocks prevention
- All magic happens at compile-time so you know right away when things go awry
- Comprehensive automated testing to make sure everything is in tip-top shape

Enumeratum is published for Scala 2.11.x, 2.12.x, 2.13.x as well as ScalaJS.

Integrations are available for:

- [Play](https://www.playframework.com/): JVM only
- [Play JSON](https://www.playframework.com/documentation/2.8.x/ScalaJson): JVM (included in Play integration but also available separately) and ScalaJS
- [Circe](https://github.com/travisbrown/circe): JVM and ScalaJS
- [ReactiveMongo BSON](http://reactivemongo.org/releases/0.1x/documentation/bson/overview.html): JVM only
- [Argonaut](http://argonaut.io): JVM and ScalaJS
- [Json4s](http://json4s.org): JVM only
- [ScalaCheck](https://www.scalacheck.org): JVM and ScalaJS
- [Slick](http://slick.lightbend.com/): JVM only
- [Quill](http://getquill.io): JVM and ScalaJS

### Table of Contents

1. [Quick start](#quick-start)
    - [SBT](#sbt)
    - [Usage](#usage)
2. [More examples](#more-examples)
    - [Enum](#enum)
      - [Manual override of name](#manual-override-of-name)
      - [Mixins to override the name](#mixins-to-override-the-name)
    - [ValueEnum](#valueenum)
3. [ScalaJS](#scalajs)
4. [Play integration](#play-integration)
5. [Play JSON integration](#play-json)
6. [Circe integration](#circe)
8. [ReactiveMongo BSON integration](#reactivemongo-bson)
9. [Argonaut integration](#argonaut)
10. [Json4s integration](#json4s)
11. [Slick integration](#slick-integration)
12. [ScalaCheck](#scalacheck)
13. [Quill integration](#quill)
14. [Cats integration](#cats)
15. [Doobie integration](#doobie)
16. [Benchmarking](#benchmarking)
17. [Publishing](#publishing)


## Quick start

### SBT

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum_2.11)

In `build.sbt`, set the Enumeratum version in a variable (for the latest version, set `val enumeratumVersion = ` the version you see
in the Maven badge above).

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum" % enumeratumVersion
)
```

Enumeratum has different integrations that can be added to your build à la carte. For more info, see the respective sections in
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

  case object Hello   extends Greeting
  case object GoodBye extends Greeting
  case object Hi      extends Greeting
  case object Bye     extends Greeting

}

// Object Greeting has a `withName(name: String)` method
Greeting.withName("Hello")
// => res0: Greeting = Hello

Greeting.withName("Haro")
// => java.lang.NoSuchElementException: Haro is not a member of Enum (Hello, GoodBye, Hi, Bye)

// A safer alternative would be to use `withNameOption(name: String)` method which returns an Option[Greeting]
Greeting.withNameOption("Hello")
// => res1: Option[Greeting] = Some(Hello)

Greeting.withNameOption("Haro")
// => res2: Option[Greeting] = None

// It is also possible to use strings case insensitively
Greeting.withNameInsensitive("HeLLo")
// => res3: Greeting = Hello

Greeting.withNameInsensitiveOption("HeLLo")
// => res4: Option[Greeting] = Some(Hello)

// Uppercase-only strings may also be used
Greeting.withNameUppercaseOnly("HELLO")
// => res5: Greeting = Hello

Greeting.withNameUppercaseOnlyOption("HeLLo")
// => res6: Option[Greeting] = None

// Similarly, lowercase-only strings may also be used
Greeting.withNameLowercaseOnly("hello")
// => res7: Greeting = Hello

Greeting.withNameLowercaseOnlyOption("hello")
// => res8: Option[Greeting] = Some(Hello)
```

Note that by default, `findValues` will return a `Seq` with the enum members listed in written-order (relevant if you want to
use the `indexOf` method).

Enum members found in nested objects will be included by `findValues` as well, and will appear in the order they are
written in the companion object, top to bottom. Note that enum members declared in traits or classes will *not* be
discovered by `findValues`. For example:

```scala
sealed trait Nesting extends EnumEntry
object Nesting extends Enum[Nesting] {
  val values = findValues

  case object Hello extends Nesting
  object others {
    case object GoodBye extends Nesting
  }
  case object Hi extends Nesting
  class InnerClass {
    case object NotFound extends Nesting
  }
}

Nesting.values
// => res0: scala.collection.immutable.IndexedSeq[Nesting] = Vector(Hello, GoodBye, Hi)
```

For an interactive demo, checkout this [Scastie snippet](https://scastie.scala-lang.org/lloydmeta/AMXiGvzkR0CD5sgWXiW4bg).

## More examples

### Enum

Continuing from the `Greeting` enum declared in [the quick-start section](#usage):

```scala
import Greeting._

def tryMatching(v: Greeting): Unit = v match {
  case Hello   => println("Hello")
  case GoodBye => println("GoodBye")
  case Hi      => println("Hi")
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
`EnumEntry`. This behavior can be changed in two ways.


#### Manual override of name
The first way to change the name behaviour is to manually override the `def entryName: String` method.

```scala

import enumeratum._

sealed abstract class State(override val entryName: String) extends EnumEntry

object State extends Enum[State] {

   val values = findValues

   case object Alabama extends State("AL")
   case object Alaska  extends State("AK")
   // and so on and so forth.
}

import State._

State.withName("AL")

```

#### Mixins to override the name

The second way to override the name behaviour is to mixin the stackable traits provided for common string
conversions, `Snakecase`, `UpperSnakecase`, `CapitalSnakecase`, `Hyphencase`, `UpperHyphencase`, `CapitalHyphencase`, `Dotcase`, `UpperDotcase`, `CapitalDotcase`, `Words`, `UpperWords`, `CapitalWords`, `Camelcase`, `LowerCamelcase`, `Uppercase`, `Lowercase`, and `Uncapitalised`.

```scala

import enumeratum._
import enumeratum.EnumEntry._

sealed trait Greeting extends EnumEntry with Snakecase

object Greeting extends Enum[Greeting] {

  val values = findValues

  case object Hello        extends Greeting
  case object GoodBye      extends Greeting
  case object ShoutGoodBye extends Greeting with Uppercase

}

Greeting.withName("hello")
Greeting.withName("good_bye")
Greeting.withName("SHOUT_GOOD_BYE")

```

### ValueEnum

Asides from enumerations that resolve members from `String` _names_, Enumeratum also supports `ValueEnum`s, enums that resolve
members from simple _values_ like `Int`, `Long`, `Short`, `Char`, `Byte`, and `String` (without support for runtime transformations).

These enums are not modelled after `Enumeration` from standard lib, and therefore have the added ability to make sure, at compile-time,
that multiple members do not share the same value.

```scala
import enumeratum.values._

sealed abstract class LibraryItem(val value: Int, val name: String) extends IntEnumEntry

object LibraryItem extends IntEnum[LibraryItem] {


  case object Book     extends LibraryItem(value = 1, name = "book")
  case object Movie    extends LibraryItem(name = "movie", value = 2)
  case object Magazine extends LibraryItem(3, "magazine")
  case object CD       extends LibraryItem(4, name = "cd")
  // case object Newspaper extends LibraryItem(4, name = "newspaper") <-- will fail to compile because the value 4 is shared

  /*
  val five = 5
  case object Article extends LibraryItem(five, name = "article") <-- will fail to compile because the value is not a literal
  */

  val values = findValues

}

assert(LibraryItem.withValue(1) == LibraryItem.Book)

LibraryItem.withValue(10) // => java.util.NoSuchElementException:
```

If you want to allow aliases in your enumeration, i.e. multiple entries that share the same value, you can extend the
`enumeratum.values.AllowAlias` trait:

```scala
import enumeratum.values._

sealed abstract class Judgement(val value: Int) extends IntEnumEntry with AllowAlias

object Judgement extends IntEnum[Judgement] {

  case object Good extends Judgement(1)
  case object OK extends Judgement(2)
  case object Meh extends Judgement(2)
  case object Bad extends Judgement(3)

  val values = findValues

}
```

Calling `withValue` with an aliased value will return one of the corresponding entries. Which one it returns is undefined:

```scala
assert(Judgement.withValue(2) == Judgement.OK || Judgement.withValue(2) == Judgement.Meh)
```

**Restrictions**
- `ValueEnum`s must have their value members implemented as literal values.


## ScalaJS

In a ScalaJS project, add the following to `build.sbt`:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum_2.11)

```scala
libraryDependencies ++= Seq(
    "com.beachape" %%% "enumeratum" % enumeratumVersion
)
```

As expected, usage is exactly the same as normal Scala.

## Play Integration
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-play_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-play_2.11)

The `enumeratum-play` project is published separately and gives you access to various tools
to help you avoid boilerplate in your Play project.

### SBT

For enumeratum with full Play support:
```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum-play" % enumeratumPlayVersion
)
```

Note that as of version 1.4.0, `enumeratum-play` for Scala 2.11 is compatible with Play 2.5 - 2.7

### Usage

#### PlayEnum

The included `PlayEnum` trait is probably going to be the most interesting as it includes a bunch
of built-in implicits like Json formats, Path bindables, Query string bindables, and Form field support.

For example:

```scala
package enums

import enumeratum._

sealed trait Greeting extends EnumEntry

object Greeting extends PlayEnum[Greeting] {

  val values = findValues

  case object Hello   extends Greeting
  case object GoodBye extends Greeting
  case object Hi      extends Greeting
  case object Bye     extends Greeting

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

#### PlayValueEnums

There are `IntPlayEnum`, `LongPlayEnum`, and `ShortPlayEnum` traits for use with `IntEnumEntry`, `LongEnumEntry`, and
`ShortEnumEntry` respectively that provide Play-specific implicits as with normal `PlayEnum`. For example:

```scala
import enumeratum.values._

sealed abstract class PlayLibraryItem(val value: Int, val name: String) extends IntEnumEntry

case object PlayLibraryItem extends IntPlayEnum[PlayLibraryItem] {

  // A good mix of named, unnamed, named + unordered args
  case object Book     extends PlayLibraryItem(value = 1, name = "book")
  case object Movie    extends PlayLibraryItem(name = "movie", value = 2)
  case object Magazine extends PlayLibraryItem(3, "magazine")
  case object CD       extends PlayLibraryItem(4, name = "cd")

  val values = findValues

}

import play.api.libs.json.{ JsNumber, JsString, Json => PlayJson }
PlayLibraryItem.values.foreach { item =>
    assert(PlayJson.toJson(item) == JsNumber(item.value))
}
```

#### PlayFormFieldEnum
`PlayEnum` extends the trait `PlayFormFieldEnum` wich offers `formField` for mapping within a `play.api.data.Form` object.

```scala
import play.api.data.Form
import play.api.data.Forms._

object GreetingForm {
  val form = Form(
    mapping(
      "name"     -> nonEmptyText,
      "greeting" -> Greeting.formField
    )(Data.apply)(Data.unapply)
  )

  case class Data(
    name: String,
    greeting: Greeting)
}
```

Another alternative (if for example your `Enum` can't extend `PlayEnum` or `PlayFormFieldEnum`) is to create an implicit `Format`
and bring it into scope using Play's `of`, i.e.

```scala
import play.api.data.Form
import play.api.data.Forms._

object Formats {
  implicit val greetingFormat = enumeratum.Forms.format(Greeting)
}

object GreetingForm {
  import Formats._

  val form = Form(
      mapping(
        "name"     -> nonEmptyText,
        "greeting" -> of[Greeting]
      )(Data.apply)(Data.unapply)
    )

    case class Data(
      name: String,
      greeting: Greeting)

}
```

## Play JSON
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-play-json_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-play-json_2.11)

The `enumeratum-play-json` project is published separately and gives you access to Play's auto-generated boilerplate
for JSON serialization in your Enum's.

### SBT

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum-play-json" % enumeratumPlayJsonVersion
)
```

Note that as of version 1.4.0, `enumeratum-play-json` for Scala 2.11 is compatible with Play 2.5 - 2.7

### Usage

#### PlayJsonEnum

There are also `PlayInsensitiveJsonEnum`, `PlayLowercaseJsonEnum`, and `PlayUppercaseJsonEnum` traits for use. For example:

```scala
import enumeratum.{ PlayJsonEnum, Enum, EnumEntry }

sealed trait Greeting extends EnumEntry

object Greeting extends Enum[Greeting] with PlayJsonEnum[Greeting] {

  val values = findValues

  case object Hello   extends Greeting
  case object GoodBye extends Greeting
  case object Hi      extends Greeting
  case object Bye     extends Greeting

}

```

#### PlayJsonValueEnum

There are `IntPlayJsonEnum`, `LongPlayJsonEnum`, and `ShortPlayJsonEnum` traits for use with `IntEnumEntry`, `LongEnumEntry`, and
`ShortEnumEntry` respectively. For example:

```scala
import enumeratum.values._

sealed abstract class JsonDrinks(val value: Short, name: String) extends ShortEnumEntry

case object JsonDrinks extends ShortEnum[JsonDrinks] with ShortPlayJsonValueEnum[JsonDrinks] {

  case object OrangeJuice extends JsonDrinks(value = 1, name = "oj")
  case object AppleJuice  extends JsonDrinks(value = 2, name = "aj")
  case object Cola        extends JsonDrinks(value = 3, name = "cola")
  case object Beer        extends JsonDrinks(value = 4, name = "beer")

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
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-circe_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-circe_2.11)

### SBT

To use enumeratum with [Circe](https://github.com/travisbrown/circe):

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum-circe" % enumeratumCirceVersion
)
```

To use with ScalaJS:

```scala
libraryDependencies ++= Seq(
    "com.beachape" %%% "enumeratum-circe" % enumeratumCirceVersion
)
```

### Usage

#### Enum

```scala
import enumeratum._

sealed trait ShirtSize extends EnumEntry

case object ShirtSize extends Enum[ShirtSize] with CirceEnum[ShirtSize] {

  case object Small  extends ShirtSize
  case object Medium extends ShirtSize
  case object Large  extends ShirtSize

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
  case object Book     extends CirceLibraryItem(value = 1, name = "book")
  case object Movie    extends CirceLibraryItem(name = "movie", value = 2)
  case object Magazine extends CirceLibraryItem(3, "magazine")
  case object CD       extends CirceLibraryItem(4, name = "cd")

  val values = findValues

}

import io.circe.Json
import io.circe.syntax._

CirceLibraryItem.values.foreach { item =>
    assert(item.asJson == Json.fromInt(item.value))
}
```

## ReactiveMongo BSON
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-reactivemongo-bson_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-reactivemongo-bson_2.11)

The `enumeratum-reactivemongo-bson` project is published separately and gives you access to ReactiveMongo's auto-generated boilerplate
for BSON serialization in your Enum's.

### SBT

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum-reactivemongo-bson" % enumeratumReactiveMongoVersion
)
```

### Usage

#### ReactiveMongoBsonEnum

For example:

```scala
import enumeratum.{ ReactiveMongoBsonEnum, Enum, EnumEntry }

sealed trait Greeting extends EnumEntry

object Greeting extends Enum[Greeting] with ReactiveMongoBsonEnum[Greeting] {

  val values = findValues

  case object Hello   extends Greeting
  case object GoodBye extends Greeting
  case object Hi      extends Greeting
  case object Bye     extends Greeting

}

```

#### ReactiveMongoBsonValueEnum

There are `IntReactiveMongoBsonValueEnum`, `LongReactiveMongoBsonValueEnum`, and `ShortReactiveMongoBsonValueEnum` traits for use with `IntEnumEntry`, `LongEnumEntry`, and
`ShortEnumEntry` respectively. For example:

```scala
import enumeratum.values._

sealed abstract class BsonDrinks(val value: Short, name: String) extends ShortEnumEntry

case object BsonDrinks extends ShortEnum[BsonDrinks] with ShortReactiveMongoBsonValueEnum[BsonDrinks] {

  case object OrangeJuice extends BsonDrinks(value = 1, name = "oj")
  case object AppleJuice  extends BsonDrinks(value = 2, name = "aj")
  case object Cola        extends BsonDrinks(value = 3, name = "cola")
  case object Beer        extends BsonDrinks(value = 4, name = "beer")

  val values = findValues

}

import reactivemongo.api.bson._

// Use to deserialise numbers to enum members directly
BsonDrinks.values.foreach { drink =>
  val writer = implicitly[BSONWriter[BsonDrinks]]

  assert(writer.write(drink) == BSONInteger(drink.value))
}

val reader = implicitly[BSONReader[BsonDrinks]]

assert(reader.read(BSONInteger(3)) == BsonDrinks.Cola)
```

## Argonaut
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-argonaut_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-argonaut_2.11)

### SBT

To use enumeratum with [Argonaut](http://www.argonaut.io):

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum-argonaut" % enumeratumArgonautVersion
)
```

### Usage

#### Enum

```scala
import enumeratum._

sealed trait TrafficLight extends EnumEntry
object TrafficLight extends Enum[TrafficLight] with ArgonautEnum[TrafficLight] {
  case object Red    extends TrafficLight
  case object Yellow extends TrafficLight
  case object Green  extends TrafficLight
  val values = findValues
}

import argonaut._
import Argonaut._

TrafficLight.values.foreach { entry =>
    assert(entry.asJson == entry.entryName.asJson)
}

```

#### ValueEnum

```scala
import enumeratum.values._

sealed abstract class ArgonautDevice(val value: Short) extends ShortEnumEntry
case object ArgonautDevice
    extends ShortEnum[ArgonautDevice]
    with ShortArgonautEnum[ArgonautDevice] {
  case object Phone   extends ArgonautDevice(1)
  case object Laptop  extends ArgonautDevice(2)
  case object Desktop extends ArgonautDevice(3)
  case object Tablet  extends ArgonautDevice(4)

  val values = findValues
}

import argonaut._
import Argonaut._

ArgonautDevice.values.foreach { item =>
    assert(item.asJson == item.value.asJson)
}
```

## Json4s
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-json4s_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-json4s_2.11)

### SBT

To use enumeratum with [Json4s](http://json4s.org):

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum-json4s" % enumeratumJson4sVersion
)
```

### Usage

#### Enum

```scala
import enumeratum._

sealed trait TrafficLight extends EnumEntry
object TrafficLight extends Enum[TrafficLight] /* nothing extra here */ {
  case object Red    extends TrafficLight
  case object Yellow extends TrafficLight
  case object Green  extends TrafficLight

  val values = findValues
}

import org.json4s.DefaultFormats

implicit val formats = DefaultFormats + Json4s.serializer(TrafficLight)

```

#### ValueEnum

```scala
import enumeratum.values._

sealed abstract class Device(val value: Short) extends ShortEnumEntry
case object Device
  extends ShortEnum[Device] /* nothing extra here */  {
  case object Phone   extends Device(1)
  case object Laptop  extends Device(2)
  case object Desktop extends Device(3)
  case object Tablet  extends Device(4)

  val values = findValues
}

import org.json4s.DefaultFormats

implicit val formats = DefaultFormats + Json4s.serializer(Device)

```

## ScalaCheck
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-scalacheck_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-scalacheck_2.11)

### SBT

To use enumeratum with [ScalaCheck](https://www.scalacheck.org):

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum-scalacheck" % enumeratumScalacheckVersion
)
```

### Usage

#### Enum

Given the enum declared in [the quick-start section](#usage), you can get an `Arbitrary[Greeting]` (to generate instances of `Greeting`) and a `Cogen[Greeting]` (to generate instances of `Greeting => (A: Arbitrary)`) by importing generators in the scope of your tests:

```scala
import enumeratum.scalacheck._
```

#### ValueEnum

Similarly, you can get `Arbitrary` and `Cogen` instances for every `ValueEnum` subtype by importing generators in the scope of your tests:

```scala
import enumeratum.values.scalacheck._
```

## Quill
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-quill_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-quill_2.11)

### SBT

To use enumeratum with [Quill](http://getquill.io):

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum-quill" % enumeratumQuillVersion
)
```

To use with ScalaJS:

```scala
libraryDependencies ++= Seq(
    "com.beachape" %%% "enumeratum-quill" % enumeratumQuillVersion
)
```

### Usage

#### Enum

```scala
import enumeratum._

sealed trait ShirtSize extends EnumEntry

case object ShirtSize extends Enum[ShirtSize] with QuillEnum[ShirtSize] {

  case object Small  extends ShirtSize
  case object Medium extends ShirtSize
  case object Large  extends ShirtSize

  val values = findValues

}

case class Shirt(size: ShirtSize)

import io.getquill._

lazy val ctx = new PostgresJdbcContext(SnakeCase, "ctx")
import ctx._

ctx.run(query[Shirt].insert(_.size -> lift(ShirtSize.Small: ShirtSize)))

ctx.run(query[Shirt]).foreach(println)
```
- Note that a type ascription to the `EnumEntry` trait (eg. `ShirtSize.Small: ShirtSize`) is required when binding hardcoded `EnumEntry`s

#### ValueEnum

```scala
import enumeratum._

sealed abstract class ShirtSize(val value: Int) extends IntEnumEntry

case object ShirtSize extends IntEnum[ShirtSize] with IntQuillEnum[ShirtSize] {

  case object Small  extends ShirtSize(1)
  case object Medium extends ShirtSize(2)
  case object Large  extends ShirtSize(3)

  val values = findValues

}

case class Shirt(size: ShirtSize)

import io.getquill._

lazy val ctx = new PostgresJdbcContext(SnakeCase, "ctx")
import ctx._

ctx.run(query[Shirt].insert(_.size -> lift(ShirtSize.Small: ShirtSize)))

ctx.run(query[Shirt]).foreach(println)
```
- Note that a type ascription to the `ValueEnumEntry` abstract class (eg. `ShirtSize.Small: ShirtSize`) is required when binding hardcoded `ValueEnumEntry`s
- `quill-cassandra` currently does not support `ShortEnum` and `ByteEnum` (see [getquill/quill#1009](https://github.com/getquill/quill/issues/1009))
- `quill-orientdb` currently does not support `ByteEnum` (see [getquill/quill#1029](https://github.com/getquill/quill/issues/1029))

## Slick integration
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-slick_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-slick_2.11)

### Column Mappings
In order to use your enumeratum Enums in Slick tables as columns, you will
 need to construct instances of `MappedColumnType` and make them available
 where you define and query your slick tables. In order to more easily
 construct these instances, the enumeratum-slick integration provides a trait
 `enumeratum.SlickEnumSupport`. This trait provides a method `mappedColumnTypeForEnum`
 (and variants) for constructing a mapped column type for your enum. For example
 if you want to use `Enum[Greeting]` in your slick table, mix in `SlickEnumSupport`
 where you define your table.
```scala
trait GreetingRepository extends SlickEnumSupport {
  val profile: slick.jdbc.Profile
  implicit lazy val greetingMapper = mappedColumnTypeForEnum(Greeting)
  class GreetingTable(tag: Tag) extends Table[(String, Greeting)](tag, "greeting") {
    def id = column[String]("id", O.PrimaryKey)
    def greeting = column[Greeting]("greeting") // Maps to a varchar/text column

    def * = (id, greeting)
  }

```

### ValueEnum Mappings

If you want to represent a `ValueEnum` by its `value` rather than its string
name, simply mix in `SlickValueEnumSupport` and proceed mostly as above:
```scala
implicit lazy val libraryItemMapper = mappedColumnTypeForIntEnum(LibraryItem)
...
def item = column[LibraryItem]("LIBRARY_ITEM") // Maps to a numeric column
```

### Common Mappers

An alternate approach which is useful when mappers need to be shared across
repositories (perhaps for something common like a "Status" enum) is to define
your mappers in a module on their own, then make use of them in your repositories:
```scala
trait CommonMappers extends SlickEnumSupport {
  val profile: Profile
  implicit lazy val statusMapper = mappedColumnTypeForEnum(Status)
  ...
}
trait UserRepository extends CommonMappers {
  val profile: Profile
  class UserTable(tag: Tag) extends Table[UserRow](tag, "user") {
    ...
    def status = column[Status]("status")
    ...
  }
}
```

### Querying by enum column types

Note that because your enum values are singleton objects, you may get errors when you try to use them in Slick queries like
the following:

```scala
.filter(_.trafficLight === TrafficLight.Red)
```

This is because `TrafficLight.Red` in the above example is inferred to
be of its unique type (`TrafficLight.Red`) rather than `TrafficLight`,
thus causing a failure to find your mapping. In order to fix this,
simply assist the compiler by ascribing the type to be `TrafficLight`:

```scala
.filter(_.trafficLight === (TrafficLight.Red: TrafficLight))
```

A way around this if you find the type expansion offensive is to define
val accessors for your enum entries that are typed as the parent type.
You can do this inside your Enums companion object or more locally:
```scala
val red: TrafficLight = Red // Not red: TrafficLight.Red = Red
val yellow: TrafficLight = Yellow
val green: TrafficLight = Green
...
.filter(_.trafficLight === red)
```

### Interpolated / Plain SQL integration

If you want to use slick interpolated SQL queries you can use the provided
constructors to instantiate instances of `GetResult[_]` and `SetParameter[_]`
for your enum:
```scala
import SlickEnumPlainSqlSupport._
```
Or mix it in...
```scala
trait Foo extends SlickEnumPlainSqlSupport {
  ...
}
```
Then define your instances:
```scala
implicit val greetingGetResult = getResultForEnum(Greeting)
implicit val greetingOptionGetResult = optionalGetResultForEnum(Greeting)
implicit val greetingSetParameter = setParameterForEnum(Greeting)
implicit val greetingOptionSetParameter = optionalSetParameterForEnum(Greeting)
```

## Cats
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-cats_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-cats_2.11)

### SBT

To use enumeratum with [Cats](https://github.com/typelevel/cats):

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum-cats" % enumeratumCatsVersion
)
```

To use with ScalaJS:

```scala
libraryDependencies ++= Seq(
    "com.beachape" %%% "enumeratum-cats" % enumeratumCatsVersion
)
```

### Usage

This enumeratum module is mostly useful for generic derivation - providing instances for `Eq`, `Show` and `Hash`. So if you have structures (for example case classes) which
contain enum values, you get the instances for the enum itself "for free". But it can be useful for standalone usage as,
providing type-safe comparison and hashing.

#### Enum

```scala
import enumeratum._

sealed trait ShirtSize extends EnumEntry

case object ShirtSize extends Enum[ShirtSize] with CatsEnum[ShirtSize] {

  case object Small   extends ShirtSize
  case object Medium  extends ShirtSize
  case object Large   extends ShirtSize

  val values = findValues

}

import cats.syntax.eq._
import cats.syntax.show._

val shirtSizeOne: ShirtSize = ...
val shirtSizeTwo: ShirtSize = ...

if(shirtSizeOne === shirtSizeTwo) { // note the third equals
    printf("We got the same size, its hash is: %i", implicitly[Hash[TrafficLight]].hash(shirtSizeOne))
} else {
    printf("Shirt sizes mismatch: %s =!= %s", shirtSizeOne.show, shirtSizeTwo.show)
}
```

#### ValueEnum

There are two implementations for `ValueEnum`:
* `CatsValueEnum` provides the same functionality as `CatsEnum` (except `Hash`)
* `CatsOrderValueEnum` provides the same functionality as `CatsValueEnum` plus an instance of `cats.Order` (due to Scala 2 trait limitations, it's an `abstract class`, check out `CatsCustomOrderValueEnum` if you need a `trait`)

```scala
import enumeratum.values._

sealed abstract class CatsPriority(val value: Int, val name: String) extends IntEnumEntry

case object CatsPriority extends IntEnum[CatsPriority] with CatsOrderValueEnum[Int, CatsPriority] {

  // A good mix of named, unnamed, named + unordered args
  case object Low         extends CatsPriority(value = 1, name = "low")
  case object Medium      extends CatsPriority(name = "medium", value = 2)
  case object High        extends CatsPriority(3, "high")
  case object SuperHigh   extends CatsPriority(4, name = "super_high")

  val values = findValues

}

import cats.instances.int._
import cats.instances.list._
import cats.syntax.order._
import cats.syntax.foldable._

val items: List[CatsPriority] = List(High, Low, SuperHigh)

items.maximumOption // Some(SuperHigh)
```

#### Inheritance-free usage
If you need instances, but hesitate to mix in the traits demonstrated above, you can get them using the provided methods in `enumeratum.Cats` and `enumeratum.values.Cats` - the second also provides more flexibility than the (opinionated) mix-in trait as it allows to pass a custom type class instance for the value type (methods names are prefixed with `value`).

## Doobie
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-doobie_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-doobie_2.11)

### SBT

To use enumeratum with [Doobie](https://github.com/tpolecat/doobie):

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum-doobie" % enumeratumDoobieVersion
)
```

To use with ScalaJS:

```scala
libraryDependencies ++= Seq(
    "com.beachape" %%% "enumeratum-doobie" % enumeratumDoobieVersion
)
```

### Usage

#### Enum
If you need to store enum values in text column of following table

```sql
CREATE TABLE clothes (
  shirt varchar(100)
)
```
you should use following code
```scala
import enumeratum._

sealed trait ShirtSize extends EnumEntry

case object ShirtSize extends Enum[ShirtSize] with DoobieEnum[ShirtSize] {

  case object Small  extends ShirtSize
  case object Medium extends ShirtSize
  case object Large  extends ShirtSize

  val values = findValues

}

case class Shirt(size: ShirtSize)

import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats.effect._

implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

val xa = Transactor.fromDriverManager[IO](
  "org.postgresql.Driver",
  "jdbc:postgresql:world",
  "postgres",
  "",
  Blocker.liftExecutionContext(ExecutionContexts.synchronous)
)

sql"insert into clothes (shirt) values (${Shirt(ShirtSize.Small)})".update.run
  .transact(xa)
  .unsafeRunSync

sql"select shirt from clothes"
  .query[Shirt]
  .to[List]
  .transact(xa)
  .unsafeRunSync
  .take(5)
  .foreach(println)
```
- Note that a type ascription to the `EnumEntry` trait (eg. `ShirtSize.Small: ShirtSize`) is required when binding hardcoded `EnumEntry`s

#### ValueEnum

```scala
import enumeratum.values.{ IntDoobieEnum, IntEnum, IntEnumEntry }

sealed abstract class ShirtSize(val value: Int) extends IntEnumEntry

case object ShirtSize extends IntEnum[ShirtSize] with IntDoobieEnum[ShirtSize] {

  case object Small  extends ShirtSize(1)
  case object Medium extends ShirtSize(2)
  case object Large  extends ShirtSize(3)

  val values = findValues

}

case class Shirt(size: ShirtSize)

import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats.effect._

implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

val xa = Transactor.fromDriverManager[IO](
  "org.postgresql.Driver",
  "jdbc:postgresql:world",
  "postgres",
  "",
  Blocker.liftExecutionContext(ExecutionContexts.synchronous)
)

sql"insert into clothes (shirt) values (${Shirt(ShirtSize.Small)})".update.run
  .transact(xa)
  .unsafeRunSync

sql"select shirt from clothes"
  .query[Shirt]
  .to[List]
  .transact(xa)
  .unsafeRunSync
  .take(5)
  .foreach(println)
```
- Note that a type ascription to the `ValueEnumEntry` abstract class (eg. `ShirtSize.Small: ShirtSize`) is required when binding hardcoded `ValueEnumEntry`s

## Benchmarking

Benchmarking is in the unpublished `benchmarking` project. It uses JMH and you can run them in the sbt console by issuing the following command from your command line:

`sbt +benchmarking/'jmh:run -i 10 -wi 10 -f3 -t 1'`

The above command will run JMH benchmarks against different versions of Scala. Leave off `+` to run against the main/latest supported version of Scala.

On my late 2013 MBP using Java8 on OSX El Capitan:

```
[info] Benchmark                                            Mode  Cnt     Score    Error  Units
[info] EnumBenchmarks.indexOf                               avgt   30    11.628 ±  0.190  ns/op
[info] EnumBenchmarks.withNameDoesNotExist                  avgt   30  1809.194 ± 33.113  ns/op
[info] EnumBenchmarks.withNameExists                        avgt   30    13.540 ±  0.374  ns/op
[info] EnumBenchmarks.withNameOptionDoesNotExist            avgt   30     5.999 ±  0.037  ns/op
[info] EnumBenchmarks.withNameOptionExists                  avgt   30     9.662 ±  0.232  ns/op
[info] StdLibEnumBenchmarks.withNameDoesNotExist            avgt   30  1921.690 ± 78.898  ns/op
[info] StdLibEnumBenchmarks.withNameExists                  avgt   30    56.517 ±  1.161  ns/op
[info] values.ValueEnumBenchmarks.withValueDoesNotExist     avgt   30  1950.291 ± 29.292  ns/op
[info] values.ValueEnumBenchmarks.withValueExists           avgt   30     4.009 ±  0.062  ns/op
[info] values.ValueEnumBenchmarks.withValueOptDoesNotExist  avgt   30     5.285 ±  0.063  ns/op
[info] values.ValueEnumBenchmarks.withValueOptExists        avgt   30     6.621 ±  0.084  ns/op
```

### Discussion

Other than the methods that throw `NoSuchElementException`s, performance is in the 10ns range (taking into account JMH overhead of roughly 2-3ns), which
is acceptable for almost all use-cases. PRs that promise to increase performance are expected to be demonstrably faster.

Also, Enumeratum's `withName` is faster than the standard library's `Enumeration`, by around 4x in the case where an entry exists with the given name.
My guess is this is because Enumeratum doesn't use any `synchronized` calls or `volatile` annotations. It is also faster in the case where there is no
corresponding name, but not by a significant amount, perhaps because the high cost of throwing an exception masks any benefits.

## Publishing

Projects are published independently of each other.

JVM + ScalaJS projects should have an aggregate project to make it easy to publish them, e.g. for `enumeratum-circe`:

`$ sbt "project circe-aggregate" +clean +publish-signed`

Should publish all needed artefacts. Note that `sbt circe-aggregate/publish-signed` will not work (ScalaJS gets skipped).
