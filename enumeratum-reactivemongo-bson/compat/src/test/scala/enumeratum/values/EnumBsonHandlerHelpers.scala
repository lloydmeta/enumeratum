package enumeratum.values

import org.scalatest._
import reactivemongo.bson._

/**
  * @author Alessandro Lacava (@lambdista)
  * @since 2016-04-23
  */
trait EnumBsonHandlerHelpers { this: FunSpec with Matchers =>

  def testWriter[EntryType <: ValueEnumEntry[ValueType], ValueType <: AnyVal: Numeric]
  (enumKind: String,
   enum: ValueEnum[ValueType, EntryType],
   providedWriter: Option[BSONWriter[EntryType, BSONValue]] = None
  ): Unit = {
    val numeric = implicitly[Numeric[ValueType]]
    val writer = providedWriter.getOrElse(EnumHandler.writer(enum))
    describe(enumKind) {
      it("should write proper BSONValue") {
        enum.values.foreach { entry =>
          writer.write(entry) shouldBe BSONInteger(numeric.toInt(entry.value))
        }
      }
    }
  }

  def testReader[EntryType <: ValueEnumEntry[ValueType], ValueType <: AnyVal: Numeric]
  (enumKind: String,
   enum: ValueEnum[ValueType, EntryType],
   providedReads: Option[BSONReader[BSONValue, EntryType]] = None
  ): Unit = {
    val numeric = implicitly[Numeric[ValueType]]
    val reader = providedReads.getOrElse(EnumHandler.reader(enum))
    describe(enumKind) {
      it("should read valid values") {
        enum.values.foreach { entry =>
          reader.read(BSONInteger(numeric.toInt(entry.value))).asOpt.value shouldBe entry
        }
      }
      it("should fail to read with invalid values") {
        reader.read(BSONInteger(Int.MaxValue)) shouldBe 'error
        reader.read(BSONString("boon")) shouldBe 'error
      }
    }
  }

  def testHandler[EntryType <: ValueEnumEntry[ValueType], ValueType <: AnyVal: Numeric]
  (enumKind: String,
   enum: ValueEnum[ValueType, EntryType],
   providedHandler: Option[BSONHandler[BSONValue, EntryType]] = None
  ): Unit = {
    val handler = providedHandler.getOrElse(EnumHandler.handler(enum))
    testReader(enumKind, enum, Some(handler))
    testWriter(enumKind, enum, Some(handler))
  }

}
