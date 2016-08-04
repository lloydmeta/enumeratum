package enumeratum.values

import org.scalatest._
import reactivemongo.bson._

/**
 * @author Alessandro Lacava (@lambdista)
 * @since 2016-04-23
 */
trait EnumBsonHandlerHelpers { this: FunSpec with Matchers =>

  def testWriter[EntryType <: ValueEnumEntry[ValueType], ValueType](
    enumKind:       String,
    enum:           ValueEnum[ValueType, EntryType],
    providedWriter: Option[BSONWriter[EntryType, BSONValue]] = None
  )(implicit baseHandler: BSONHandler[BSONValue, ValueType]): Unit = {
    val writer = providedWriter.getOrElse(EnumHandler.writer(enum))
    describe(enumKind) {
      it("should write proper BSONValue") {
        enum.values.foreach { entry =>
          writer.write(entry) shouldBe baseHandler.write(entry.value)
        }
      }
    }
  }

  def testReader[EntryType <: ValueEnumEntry[ValueType], ValueType](
    enumKind:       String,
    enum:           ValueEnum[ValueType, EntryType],
    providedReader: Option[BSONReader[BSONValue, EntryType]] = None
  )(implicit baseHandler: BSONHandler[BSONValue, ValueType]): Unit = {
    val reader = providedReader.getOrElse(EnumHandler.reader(enum))
    describe(enumKind) {
      it("should read valid values") {
        enum.values.foreach { entry =>
          reader.read(baseHandler.write(entry.value)) shouldBe entry
        }
      }
      it("should fail to read with invalid values") {
        reader.readTry(BSONInteger(Int.MaxValue)) shouldBe 'failure
        reader.readTry(BSONString("boon")) shouldBe 'failure
      }
    }
  }

  def testHandler[EntryType <: ValueEnumEntry[ValueType], ValueType](
    enumKind:        String,
    enum:            ValueEnum[ValueType, EntryType],
    providedHandler: Option[BSONHandler[BSONValue, EntryType]] = None
  )(implicit baseHandler: BSONHandler[BSONValue, ValueType]): Unit = {
    val handler = providedHandler.getOrElse(EnumHandler.handler(enum))
    describe(s"$enumKind Handler") {
      testReader(enumKind, enum, Some(handler))
      testWriter(enumKind, enum, Some(handler))
    }
  }

}
