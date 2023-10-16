package enumeratum.values

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.BeforeAndAfterAll
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time._
import slick.jdbc.{GetResult, SetParameter}

class SlickValueEnumSupportSpec
    extends AnyFreeSpec
    with ScalaFutures
    with Matchers
    with BeforeAndAfterAll {

  case class ValueEnumRow(
      id: String,
      intValue: IntValueExample,
      longValue: LongValueExample,
      shortValue: ShortValueExample,
      stringValue: StringValueExample,
      byteValue: ByteValueExample,
      charValue: CharValueExample
  )

  val zeroRow = ValueEnumRow(
    "0",
    IntValueExample.Zero,
    LongValueExample.Zero,
    ShortValueExample.Zero,
    StringValueExample.Zero,
    ByteValueExample.Zero,
    CharValueExample.Zero
  )

  val oneRow = ValueEnumRow(
    "1",
    IntValueExample.One,
    LongValueExample.One,
    ShortValueExample.One,
    StringValueExample.One,
    ByteValueExample.One,
    CharValueExample.One
  )

  trait ValueEnumRepository extends SlickValueEnumSupport {

    import profile.api._

    implicit val intValueColumn: profile.BaseColumnType[IntValueExample] =
      mappedColumnTypeForValueEnum(IntValueExample)
    implicit val intSetParam: SetParameter[IntValueExample] =
      setParameterForIntEnum(IntValueExample)
    implicit val intOptionalSetParam: SetParameter[Option[IntValueExample]] =
      optionalSetParameterForIntEnum(IntValueExample)
    implicit val intGetResult: GetResult[IntValueExample] =
      getResultForIntEnum(IntValueExample)
    implicit val intOptionalGetResult: GetResult[Option[IntValueExample]] =
      optionalGetResultForIntEnum(IntValueExample)

    implicit val longValueColumn: profile.BaseColumnType[LongValueExample] =
      mappedColumnTypeForValueEnum(LongValueExample)
    implicit val longSetParam: SetParameter[LongValueExample] =
      setParameterForLongEnum(LongValueExample)
    implicit val longOptionalSetParam: SetParameter[Option[LongValueExample]] =
      optionalSetParameterForLongEnum(LongValueExample)
    implicit val longGetResult: GetResult[LongValueExample] =
      getResultForLongEnum(LongValueExample)

    implicit val shortValueColumn: profile.BaseColumnType[ShortValueExample] =
      mappedColumnTypeForValueEnum(ShortValueExample)
    implicit val shortSetParam: SetParameter[ShortValueExample] =
      setParameterForShortEnum(ShortValueExample)
    implicit val shortGetResult: GetResult[ShortValueExample] =
      getResultForShortEnum(ShortValueExample)
    implicit val shortOptionalSetParam: SetParameter[Option[ShortValueExample]] =
      optionalSetParameterForShortEnum(ShortValueExample)

    implicit val stringValueColumn: profile.BaseColumnType[StringValueExample] =
      mappedColumnTypeForValueEnum(StringValueExample)
    implicit val stringSetParam: SetParameter[StringValueExample] =
      setParameterForStringEnum(StringValueExample)
    implicit val stringOptionalSetParam: SetParameter[Option[StringValueExample]] =
      optionalSetParameterForStringEnum(StringValueExample)
    implicit val stringGetResult: GetResult[StringValueExample] =
      getResultForStringEnum(StringValueExample)

    implicit val byteValueColumn: profile.BaseColumnType[ByteValueExample] =
      mappedColumnTypeForValueEnum(ByteValueExample)
    implicit val byteSetParam: SetParameter[ByteValueExample] =
      setParameterForByteEnum(ByteValueExample)
    implicit val byteGetResult: GetResult[ByteValueExample] =
      getResultForByteEnum(ByteValueExample)
    implicit val byteOptionalSetParam: SetParameter[Option[ByteValueExample]] =
      optionalSetParameterForByteEnum(ByteValueExample)

    implicit val charValueColumn: profile.BaseColumnType[CharValueExample] =
      mappedColumnTypeForValueEnum(CharValueExample)
    implicit val charSetParam: SetParameter[CharValueExample] =
      setParameterForCharEnum(CharValueExample)
    implicit val charGetResult: GetResult[CharValueExample] =
      getResultForCharEnum(CharValueExample)
    implicit val charOptionalSetParam: SetParameter[Option[CharValueExample]] =
      optionalSetParameterForCharEnum(CharValueExample)

    class ValueEnumTable(tag: Tag) extends Table[ValueEnumRow](tag, "value_enum") {
      def id =
        column[String]("id", O.PrimaryKey)
      def intValue =
        column[IntValueExample]("int_value")
      def longValue =
        column[LongValueExample]("long_value")
      def shortValue =
        column[ShortValueExample]("short_value")
      def stringValue =
        column[StringValueExample]("string_value")
      def byteValue =
        column[ByteValueExample]("byte_value")
      def charValue =
        column[CharValueExample]("char_value")

      def * =
        (
          id,
          intValue,
          longValue,
          shortValue,
          stringValue,
          byteValue,
          charValue
        ) <> ((ValueEnumRow.apply _).tupled, ValueEnumRow.unapply)

    }
    val valueEnums = TableQuery[ValueEnumTable]
  }
  class ConcreteRepository(val profile: slick.jdbc.H2Profile) extends ValueEnumRepository

  val repo = new ConcreteRepository(slick.jdbc.H2Profile)
  import repo.profile.api._
  import repo.valueEnums
  val db = Database.forURL(
    url = "jdbc:h2:mem:test",
    driver = "org.h2.Driver",
    keepAliveConnection = true
  )

  implicit val defaultPatience: PatienceConfig = PatienceConfig(timeout = Span(1, Second))

  override def beforeAll(): Unit = {
    db.run(valueEnums.schema.create).futureValue(Timeout(Span(3, Seconds)))
  }

  override def afterAll(): Unit = {
    db.close()
  }

  "SlickValueEnumSupport" - {
    "allows creation of working column mappers for value enums" - {
      "Insertion works" in {
        db.run(valueEnums += zeroRow).futureValue shouldBe 1
      }
      "Querying works" in {
        import repo.intValueColumn
        db.run(valueEnums.result.head).futureValue shouldBe zeroRow
        val filterQuery = valueEnums
          .filter(_.intValue === (IntValueExample.Zero: IntValueExample))
          .result
          .head
        db.run(filterQuery).futureValue shouldBe zeroRow
      }
      "Value columns are actually mapped as specified" - {
        "int" in {
          val selectZeroAsInteger =
            sql"""select "int_value" from "value_enum" where "id" = '0'""".as[Int]
          db.run(selectZeroAsInteger).futureValue.head shouldBe IntValueExample.Zero.value
        }
        "long" in {
          val selectZeroAsLong =
            sql"""select "long_value" from "value_enum" where "id" = '0'""".as[Long]
          db.run(selectZeroAsLong).futureValue.head shouldBe LongValueExample.Zero.value
        }
        "short" in {
          val selectZero =
            sql"""select "short_value" from "value_enum" where "id" = '0'""".as[Short]
          db.run(selectZero).futureValue.head shouldBe ShortValueExample.Zero.value
        }
        "string" in {
          val selectZero =
            sql"""select "string_value" from "value_enum" where "id" = '0'""".as[String]
          db.run(selectZero).futureValue.head shouldBe StringValueExample.Zero.value
        }
        "byte" in {
          val selectZero =
            sql"""select "byte_value" from "value_enum" where "id" = '0'""".as[Byte]
          db.run(selectZero).futureValue.head shouldBe ByteValueExample.Zero.value
        }
        "char" in {
          // Using .as[String] since slick does not provide a GetResult[Char]
          val selectZero =
            sql"""select "char_value" from "value_enum" where "id" = '0'""".as[String]
          db.run(selectZero).futureValue.head.head shouldBe CharValueExample.Zero.value
        }

      }
    }
    "allows creation of working GetResult[_] and SetParameter[_] for value enums" - {
      import repo._
      "int" in {
        val select =
          sql"""
          select "int_value" from "value_enum"
          where "int_value" = ${IntValueExample.Zero}
            """.as[IntValueExample]
        val selectOpt =
          sql"""
          select "int_value" from "value_enum"
          where "int_value" = ${Some(IntValueExample.Zero)}
            """.as[Option[IntValueExample]]
        db.run(select).futureValue.head shouldBe IntValueExample.Zero
        db.run(selectOpt).futureValue.head shouldBe Some(IntValueExample.Zero)
      }
      "long" in {
        val select =
          sql"""
          select "long_value" from "value_enum"
          where "long_value" = ${LongValueExample.Zero}
            """.as[LongValueExample]
        db.run(select).futureValue.head shouldBe LongValueExample.Zero
      }
      "short" in {
        val select =
          sql"""
          select "short_value" from "value_enum"
          where "short_value" = ${ShortValueExample.Zero}
            """.as[ShortValueExample]
        db.run(select).futureValue.head shouldBe ShortValueExample.Zero
      }
      "string" in {
        val select =
          sql"""
          select "string_value" from "value_enum"
          where "string_value" = ${StringValueExample.Zero}
            """.as[StringValueExample]
        db.run(select).futureValue.head shouldBe StringValueExample.Zero
      }
      "byte" in {
        val select =
          sql"""
          select "byte_value" from "value_enum"
          where "byte_value" = ${ByteValueExample.Zero}
            """.as[ByteValueExample]
        db.run(select).futureValue.head shouldBe ByteValueExample.Zero
      }
      "char" in {
        // where "char_value" = ${CharValueExample.Zero}
        // where "id" = '0'
        val select =
          sql"""
          select "char_value" from "value_enum"
          where "char_value" = ${CharValueExample.Zero}
            """.as[CharValueExample]
        // println("************** " + db.run(select).futureValue.head)
        db.run(select).futureValue.head shouldBe CharValueExample.Zero
      }

    }
  }
}

sealed abstract class IntValueExample(val value: Int) extends IntEnumEntry
object IntValueExample extends IntEnum[IntValueExample] {
  val values = findValues

  case object Zero extends IntValueExample(0)
  case object One  extends IntValueExample(1)
}

sealed abstract class LongValueExample(val value: Long) extends LongEnumEntry
object LongValueExample extends LongEnum[LongValueExample] {
  val values = findValues

  case object Zero extends LongValueExample(0L)
  case object One  extends LongValueExample(1L)
}

sealed abstract class ShortValueExample(val value: Short) extends ShortEnumEntry
object ShortValueExample extends ShortEnum[ShortValueExample] {
  val values = findValues

  case object Zero extends ShortValueExample(0)
  case object One  extends ShortValueExample(1)
}
sealed abstract class StringValueExample(val value: String) extends StringEnumEntry
object StringValueExample extends StringEnum[StringValueExample] {
  val values = findValues

  case object Zero extends StringValueExample("0")
  case object One  extends StringValueExample("1")
}
sealed abstract class ByteValueExample(val value: Byte) extends ByteEnumEntry
object ByteValueExample extends ByteEnum[ByteValueExample] {
  val values = findValues

  case object Zero extends ByteValueExample(0)
  case object One  extends ByteValueExample(1)
}
sealed abstract class CharValueExample(val value: Char) extends CharEnumEntry
object CharValueExample extends CharEnum[CharValueExample] {
  val values = findValues

  case object Zero extends CharValueExample('0')
  case object One  extends CharValueExample('1')
}
