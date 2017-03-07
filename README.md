# Enumeratum [![Build Status](https://travis-ci.org/lloydmeta/enumeratum.svg?branch=master)](https://travis-ci.org/lloydmeta/enumeratum) [![Coverage Status](https://coveralls.io/repos/lloydmeta/enumeratum/badge.svg?branch=master)](https://coveralls.io/r/lloydmeta/enumeratum?branch=master) [![Codacy Badge](https://www.codacy.com/project/badge/a71a20d8678f4ed3a5b74b0659c1bc4c)](https://www.codacy.com/public/lloydmeta/enumeratum) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum_2.11) [![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.0.svg)](https://www.scala-js.org) [![Join the chat at https://gitter.im/lloydmeta/enumeratum](https://badges.gitter.im/lloydmeta/enumeratum.svg)](https://gitter.im/lloydmeta/enumeratum?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


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

Enumeratum is published for Scala 2.10.x, 2.11.x, and 2.12.x as well as ScalaJS.

Integrations are available for:

- [Play](https://www.playframework.com/): JVM only
- [Play JSON](https://www.playframework.com/documentation/2.5.x/ScalaJson): JVM only (included in Play integration but also available separately)
- [Circe](https://github.com/travisbrown/circe): JVM and ScalaJS
- [UPickle](http://www.lihaoyi.com/upickle-pprint/upickle/): JVM and ScalaJS
- [ReactiveMongo BSON](http://reactivemongo.org/releases/0.11/documentation/bson/overview.html): JVM only
- [Argonaut](http://www.argonaut.io): JVM only
- [Json4s](http://json4s.org): JVM only

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
7. [ReactiveMongo BSON integration](#reactivemongo-bson)
8. [Argonaut integration](#argonaut)
9. [Json4s integration](#json4s)
10. [Slick integration](#slick-integration)
11. [Benchmarking](#benchmarking)
12. [Publishing](#publishing)


## Quick start

### SBT

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
// => java.lang.IllegalArgumentException: Haro is not a member of Enum (Hello, GoodBye, Hi, Bye)

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


## More examples

### Enum

Continuing from the enum declared in [the quick-start section](#usage):

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
conversions, `Snakecase`, `UpperSnakecase`, `CapitalSnakecase`, `Hyphencase`, `UpperHyphencase`, `CapitalHyphencase`, `Dotcase`, `UpperDotcase`, `CapitalDotcase`, `Words`, `UpperWords`, `CapitalWords`, `Uppercase`, and `Lowercase`.

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

case object LibraryItem extends IntEnum[LibraryItem] {

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

**Restrictions**
- `ValueEnum`s must have their value members implemented as literal values.


## ScalaJS

In a ScalaJS project, add the following to `build.sbt`:

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
    "com.beachape" %% "enumeratum-play" % enumeratumVersion
)
```

Note that as of version 1.4.0, `enumeratum-play` for Scala 2.11 is compatible with Play 2.5+ while 2.10 is compatible with
Play 2.4.x. Versions prior to 1.4.0 are compatible with 2.4.x.

### Usage

#### PlayEnum

The included `PlayEnum` trait is probably going to be the most interesting as it includes a bunch
of built-in implicits like Json formats, Path bindables, Query string bindables, and Form field support.

For example:

```scala
package enums._

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
    "com.beachape" %% "enumeratum-play-json" % enumeratumVersion
)
```

Note that as of version 1.4.0, `enumeratum-play` for Scala 2.11 is compatible with Play 2.5+ while 2.10 is compatible with
Play 2.4.x. Versions prior to 1.4.0 are compatible with 2.4.x.

### Usage

#### PlayJsonEnum

For example:

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
    "com.beachape" %% "enumeratum-circe" % enumeratumVersion
)
```

To use with ScalaJS:

```scala
libraryDependencies ++= Seq(
    "com.beachape" %%% "enumeratum-circe" % enumeratumVersion
)
```

### Usage

#### Enum

```scala
import enumeratum._

sealed trait ShirtSize extends EnumEntry

case object ShirtSize extends CirceEnum[ShirtSize] with Enum[ShirtSize] {

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

## UPickle
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-upickle_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-upickle_2.11)

### SBT

To use enumeratum with [uPickle](http://lihaoyi.github.io/upickle/):

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum-upickle" % enumeratumVersion
)
```

To use with ScalaJS:

```scala
libraryDependencies ++= Seq(
    "com.beachape" %%% "enumeratum-upickle" % enumeratumVersion
)
```

### Usage

`UPickleEnum` works pretty much the same as `CirceEnum` and `PlayJsonEnum` variants, so we'll skip straight to the
`ValueEnum` integration.

```scala
import enumeratum.values._

sealed abstract class ContentType(val value: Long, name: String) extends LongEnumEntry

case object ContentType
    extends LongEnum[ContentType]
    with LongUPickleEnum[ContentType] {

  val values = findValues

  case object Text  extends ContentType(value = 1L, name = "text")
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

## ReactiveMongo BSON
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-reactivemongo-bson_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-reactivemongo-bson_2.11)

The `enumeratum-reactivemongo-bson` project is published separately and gives you access to ReactiveMongo's auto-generated boilerplate
for BSON serialization in your Enum's.

### SBT

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum-reactivemongo-bson" % enumeratumVersion
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

import reactivemongo.bson._

// Use to deserialise numbers to enum members directly
BsonDrinks.values.foreach { drink =>
  val writer = implicitly[BSONWriter[BsonDrinks, BSONValue]]

  assert(writer.write(drink) == BSONInteger(drink.value))
}

val reader = implicitly[BSONReader[BSONValue, BsonDrinks]]

assert(reader.read(BSONInteger(3)) == BsonDrinks.Cola)
```

## Argonaut
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-argonaut_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.beachape/enumeratum-argonaut_2.11)

### SBT

To use enumeratum with [Argonaut](http://www.argonaut.io):

```scala
libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum-argonaut" % enumeratumVersion
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
    "com.beachape" %% "enumeratum-json4s" % enumeratumVersion
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

## Slick integration

[Slick](http://slick.lightbend.com) doesn't have a separate integration at the moment. You just have to provide a `MappedColumnType` for each database column that should be represented as an enum on the Scala side.

For example when you want the `Enum[Greeting]` defined in the introduction as a database column, you can use the following code

```scala
  implicit lazy val greetingMapper = MappedColumnType.base[Greeting, String](
    greeting => greeting.entryName,
    string => Greeting.withName(string)
  )
```

You can then define the following line in your ```Table[...]``` class

```scala
  // This maps a column of type VARCHAR/TEXT to enums of type [[Greeting]]
  def greeting = column[Greeting]("GREETING")
```

If you want to represent your enum in the database with numeric IDs, just provide a different mapping. This example uses the enum of type `LibraryItem` defined in the introduction:

```scala
  implicit lazy val libraryItemMapper = MappedColumnType.base[LibraryItem, Int](
    item => item.value,
    id => LibraryItem.withValue(id)
  )
```

Again you can now simply use `LibraryItem` in your `Table` class:

```scala
  // This maps a column of type NUMBER to enums of type [[LibaryItem]]
  def item = column[LibraryItem]("LIBRARY_ITEM")
```

Note that because your enum values are singleton objects, you may get errors when you try to use them in Slick queries like
the following:


```scala
.filter(_.productType === ProductType.Foo)`
```

This is because `ProductType.Foo` in the above example is inferred to be of its unique type (`ProductType.Foo`) rather than `ProductType`,
thus causing a failure to find your mapping. In order to fix this, simply assist the compiler by ascribing the type to be `ProductType`:

```scala
.filter(_.productType === (ProductType.Foo: ProductType))`
```

If you want to use slick interpolated SQL queries you need a few additional implicits.

``` scala
implicit val greetingGetResult: GetResult[Greeting] = new GetResult[Greeting] {
  override def apply(positionedResult: PositionedResult): Greeting = Greeting.withName(positionedResult.nextString())
}

implicit val greetingOptionGetResult: GetResult[Option[Greeting]] = new GetResult[Option[Greeting]] {
  override def apply(positionedResult: PositionedResult): Option[Greeting] = positionedResult.nextStringOption().flatMap(Greeting.withNameOption)
}

implicit val greetingSetParameter: SetParameter[Greeting] = new SetParameter[Greeting] {
  override def apply(value: Greeting, positionedParameter: PositionedParameters): Unit =
    positionedParameter.setString(value.toString)
}

implicit val greetingOptionSetParameter: SetParameter[Option[Greeting]] = new SetParameter[Option[Greeting]] {
  override def apply(value: Option[Greeting], positionedParameter: PositionedParameters): Unit =
    positionedParameter.setStringOption(value.map(v => value.entryName))
}
```

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
