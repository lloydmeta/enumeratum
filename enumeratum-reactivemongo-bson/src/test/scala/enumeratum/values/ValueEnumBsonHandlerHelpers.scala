package enumeratum.values

import scala.util.Success

import reactivemongo.api.bson.{
  BSONHandler,
  BSONInteger,
  BSONReader,
  BSONString,
  BSONWriter,
  KeyReader,
  KeyWriter
}

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** @author
  *   Alessandro Lacava (@lambdista)
  * @since 2016-04-23
  */
trait ValueEnumBsonHandlerHelpers { this: AnyFunSpec with Matchers =>

  def testWriter[EntryType <: ValueEnumEntry[ValueType], ValueType](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType],
      providedWriter: Option[BSONWriter[EntryType]] = None
  )(implicit baseHandler: BSONHandler[ValueType]): Unit = {
    val writer = providedWriter.getOrElse(EnumHandler.writer(myEnum))

    describe(enumKind) {
      it("should write proper BSONValue") {
        myEnum.values.foreach { entry =>
          writer.writeTry(entry) shouldBe baseHandler.writeTry(entry.value)
        }
      }
    }
  }

  def testKeyWriter[EntryType <: ValueEnumEntry[ValueType], ValueType](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType],
      providedWriter: Option[KeyWriter[EntryType]] = None
  )(implicit baseHandler: KeyWriter[ValueType]): Unit = {
    val writer = providedWriter.getOrElse(EnumHandler.keyWriter(myEnum))

    describe(enumKind) {
      it("should write proper key") {
        myEnum.values.foreach { entry =>
          writer.writeTry(entry) shouldBe baseHandler.writeTry(entry.value)
        }
      }
    }
  }

  def testReader[EntryType <: ValueEnumEntry[ValueType], ValueType](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType],
      providedReader: Option[BSONReader[EntryType]] = None
  )(implicit baseHandler: BSONHandler[ValueType]): Unit = {
    val reader = providedReader.getOrElse(EnumHandler.reader(myEnum))

    describe(enumKind) {
      it("should read valid values") {
        myEnum.values.foreach { entry =>
          reader.readTry(baseHandler.writeTry(entry.value).get).get shouldBe entry
        }
      }

      it("should fail to read with invalid values") {
        reader.readTry(BSONInteger(Int.MaxValue)) shouldBe Symbol("failure")
        reader.readTry(BSONString("boon")) shouldBe Symbol("failure")
      }
    }
  }

  def testKeyReader[EntryType <: ValueEnumEntry[ValueType], ValueType](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType],
      providedReader: Option[KeyReader[EntryType]] = None
  )(implicit baseWriter: KeyWriter[ValueType], baseReader: KeyReader[ValueType]): Unit = {
    val reader = providedReader.getOrElse(EnumHandler.keyReader(myEnum))

    describe(enumKind) {
      it("should read valid key") {
        myEnum.values.foreach { entry =>
          baseWriter.writeTry(entry.value).flatMap(reader.readTry) shouldBe Success(entry)
        }
      }

      it("should fail to read with invalid key") {
        reader.readTry(Int.MaxValue.toString) shouldBe Symbol("failure")
        reader.readTry("boon") shouldBe Symbol("failure")
      }
    }
  }

  def testHandler[EntryType <: ValueEnumEntry[ValueType], ValueType](
      enumKind: String,
      myEnum: ValueEnum[ValueType, EntryType],
      providedHandler: Option[BSONHandler[EntryType]] = None
  )(implicit baseHandler: BSONHandler[ValueType]): Unit = {
    val handler = providedHandler.getOrElse(EnumHandler.handler(myEnum))
    describe(s"$enumKind Handler") {
      testReader(enumKind, myEnum, Some(handler))
      testWriter(enumKind, myEnum, Some(handler))
    }
  }

}
