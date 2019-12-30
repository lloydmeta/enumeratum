package enumeratum.values

import org.scalatest._
import reactivemongo.api.bson.{BSONHandler, BSONInteger, BSONReader, BSONString, BSONWriter}

/**
  * @author Alessandro Lacava (@lambdista)
  * @since 2016-04-23
  */
trait EnumBsonHandlerHelpers { this: FunSpec with Matchers =>

  def testWriter[EntryType <: ValueEnumEntry[ValueType], ValueType](
      enumKind: String,
      enum: ValueEnum[ValueType, EntryType],
      providedWriter: Option[BSONWriter[EntryType]] = None
  )(implicit baseHandler: BSONHandler[ValueType]): Unit = {
    val writer = providedWriter.getOrElse(EnumHandler.writer(enum))
    describe(enumKind) {
      it("should write proper BSONValue") {
        enum.values.foreach { entry =>
          writer.writeTry(entry) shouldBe baseHandler.writeTry(entry.value)
        }
      }
    }
  }

  def testReader[EntryType <: ValueEnumEntry[ValueType], ValueType](
      enumKind: String,
      enum: ValueEnum[ValueType, EntryType],
      providedReader: Option[BSONReader[EntryType]] = None
  )(implicit baseHandler: BSONHandler[ValueType]): Unit = {
    val reader = providedReader.getOrElse(EnumHandler.reader(enum))
    describe(enumKind) {
      it("should read valid values") {
        enum.values.foreach { entry =>
          reader.readTry(baseHandler.writeTry(entry.value).get).get shouldBe entry
        }
      }
      it("should fail to read with invalid values") {
        reader.readTry(BSONInteger(Int.MaxValue)) shouldBe 'failure
        reader.readTry(BSONString("boon")) shouldBe 'failure
      }
    }
  }

  def testHandler[EntryType <: ValueEnumEntry[ValueType], ValueType](
      enumKind: String,
      enum: ValueEnum[ValueType, EntryType],
      providedHandler: Option[BSONHandler[EntryType]] = None
  )(implicit baseHandler: BSONHandler[ValueType]): Unit = {
    val handler = providedHandler.getOrElse(EnumHandler.handler(enum))
    describe(s"$enumKind Handler") {
      testReader(enumKind, enum, Some(handler))
      testWriter(enumKind, enum, Some(handler))
    }
  }

}
