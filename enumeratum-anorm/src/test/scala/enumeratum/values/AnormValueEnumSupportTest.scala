package enumeratum.values
import anorm.SqlParser._
import anorm._
import org.scalatest.{BeforeAndAfterAll, FreeSpec, Matchers}
import play.api.db.Databases

class AnormValueEnumSupportTest
    extends FreeSpec
    with Matchers
    with AnormValueEnumSupport
    with BeforeAndAfterAll {
  val db = Databases.inMemory()

  implicit val toStatementIntEnum = toStatementForValueEnum(IntValueExample)
  implicit val columnIntEnum      = columnForValueEnum(IntValueExample)

  implicit val toStatementLongEnum = toStatementForValueEnum(LongValueExample)
  implicit val columnLongEnum      = columnForValueEnum(LongValueExample)

  implicit val toStatementByteEnum = toStatementForValueEnum(ByteValueExample)
  implicit val columnByteEnum      = columnForValueEnum(ByteValueExample)

  implicit val toStatementShortEnum = toStatementForValueEnum(ShortValueExample)
  implicit val columnShortEnum      = columnForValueEnum(ShortValueExample)

  implicit val toStatementCharEnum = toStatementForValueEnum(CharValueExample)
  implicit val columnCharEnum      = columnForValueEnum(CharValueExample)

  implicit val toStatementStringEnum = toStatementForValueEnum(StringValueExample)
  implicit val columnStringEnum      = columnForValueEnum(StringValueExample)

  val valueEnumParser = int("id") ~ get[IntValueExample]("int_value") ~ get[LongValueExample](
    "long_value") ~ get[ByteValueExample]("byte_value") ~ get[ShortValueExample]("short_value") ~ get[
    CharValueExample]("char_value") ~ get[StringValueExample]("string_value")

  val intZero: IntValueExample       = IntValueExample.Zero
  val longZero: LongValueExample     = LongValueExample.Zero
  val byteZero: ByteValueExample     = ByteValueExample.Zero
  val shortZero: ShortValueExample   = ShortValueExample.Zero
  val charZero: CharValueExample     = CharValueExample.Zero
  val stringZero: StringValueExample = StringValueExample.Zero

  val intOne: IntValueExample       = IntValueExample.One
  val longOne: LongValueExample     = LongValueExample.One
  val byteOne: ByteValueExample     = ByteValueExample.One
  val shortOne: ShortValueExample   = ShortValueExample.One
  val charOne: CharValueExample     = CharValueExample.One
  val stringOne: StringValueExample = StringValueExample.One

  "AnormEnumSupport" - {
    "allows creation of working Column[_] for value enums" - {
      "zero" in {
        val id ~ intEnum ~ longEnum ~ byteEnum ~ shortEnum ~ charEnum ~ stringEnum =
          db.withConnection { implicit c =>
            SQL"select * from value_enum where id=0".as(valueEnumParser.single)
          }
        id shouldBe 0
        intEnum shouldBe intZero
        longEnum shouldBe longZero
        byteEnum shouldBe byteZero
        shortEnum shouldBe shortZero
        charEnum shouldBe charZero
        stringEnum shouldBe stringZero
      }
      "one" in {
        val id ~ intEnum ~ longEnum ~ byteEnum ~ shortEnum ~ charEnum ~ stringEnum =
          db.withConnection { implicit c =>
            SQL"select * from value_enum where id=1".as(valueEnumParser.single)
          }
        id shouldBe 1
        intEnum shouldBe intOne
        longEnum shouldBe longOne
        byteEnum shouldBe byteOne
        shortEnum shouldBe shortOne
        charEnum shouldBe charOne
        stringEnum shouldBe stringOne
      }
    }
    "allows creation of working ToStatement[_] for value enums" - {
      "int" in {
        db.withConnection { implicit c =>
          SQL"select id from value_enum where int_value=$intZero"
            .as(scalar[Int].single)
        } shouldBe 0
        db.withConnection { implicit c =>
          SQL"select id from value_enum where int_value=$intOne"
            .as(scalar[Int].single)
        } shouldBe 1
      }
      "long" in {
        db.withConnection { implicit c =>
          SQL"select id from value_enum where long_value=$longZero"
            .as(scalar[Int].single)
        } shouldBe 0
        db.withConnection { implicit c =>
          SQL"select id from value_enum where long_value=$longOne"
            .as(scalar[Int].single)
        } shouldBe 1
      }
      "byte" in {
        db.withConnection { implicit c =>
          SQL"select id from value_enum where byte_value=$byteZero"
            .as(scalar[Int].single)
        } shouldBe 0
        db.withConnection { implicit c =>
          SQL"select id from value_enum where byte_value=$byteOne"
            .as(scalar[Int].single)
        } shouldBe 1
      }
      "short" in {
        db.withConnection { implicit c =>
          SQL"select id from value_enum where short_value=$shortZero"
            .as(scalar[Int].single)
        } shouldBe 0
        db.withConnection { implicit c =>
          SQL"select id from value_enum where short_value=$shortOne"
            .as(scalar[Int].single)
        } shouldBe 1
      }
      "char" in {
        db.withConnection { implicit c =>
          SQL"select id from value_enum where string_value=$charZero"
            .as(scalar[Int].single)
        } shouldBe 0
        db.withConnection { implicit c =>
          SQL"select id from value_enum where string_value=$charOne"
            .as(scalar[Int].single)
        } shouldBe 1
      }
      "string" in {
        db.withConnection { implicit c =>
          SQL"select id from value_enum where int_value=$stringZero"
            .as(scalar[Int].single)
        } shouldBe 0
        db.withConnection { implicit c =>
          SQL"select id from value_enum where int_value=$stringOne"
            .as(scalar[Int].single)
        } shouldBe 1
      }
    }
    "knows how to handle corner cases" - {
      "fail to get enum from NULL" in {
        a[AnormException] should be thrownBy db.withConnection { implicit c =>
          SQL"select * from value_enum where id=3".as(
            (
              get[IntValueExample]("int_value") |
                get[LongValueExample]("long_value") |
                get[ByteValueExample]("byte_value") |
                get[ShortValueExample]("short_value") |
                get[CharValueExample]("char_value") |
                get[StringValueExample]("string_value")
            ).single)
        }
      }
      "fail to get unsupported value" in {
        val exception = the[AnormException] thrownBy db.withConnection { implicit c =>
          SQL"select * from value_enum where id=2".as(
            (
              get[IntValueExample]("int_value") |
                get[LongValueExample]("long_value") |
                get[ByteValueExample]("byte_value") |
                get[ShortValueExample]("short_value") |
                get[CharValueExample]("char_value") |
                get[StringValueExample]("string_value")
            ).single)
        }
        exception.message should startWith("TypeDoesNotMatch")
      }
      "succeed when get option enum from NULL" in {
        val intEnum ~ longEnum ~ byteEnum ~ shortEnum ~ charEnum ~ stringEnum = db.withConnection {
          implicit c =>
            SQL"select * from value_enum where id=3".as(
              (
                get[Option[IntValueExample]]("int_value") ~
                  get[Option[LongValueExample]]("long_value") ~
                  get[Option[ByteValueExample]]("byte_value") ~
                  get[Option[ShortValueExample]]("short_value") ~
                  get[Option[CharValueExample]]("char_value") ~
                  get[Option[StringValueExample]]("string_value")
              ).single)
        }
        intEnum shouldBe empty
        longEnum shouldBe empty
        byteEnum shouldBe empty
        shortEnum shouldBe empty
        charEnum shouldBe empty
        stringEnum shouldBe empty
      }
    }

  }

  override protected def beforeAll(): Unit = {
    db.withConnection { implicit c =>
      SQL("""CREATE TABLE value_enum (
            |   id INT NOT NULL,
            |   int_value int,
            |   long_value bigint,
            |   byte_value tinyint,
            |   short_value smallint,
            |   char_value char,
            |   string_value varchar(10)
            |)""".stripMargin)
        .execute()
      db.withConnection { implicit c =>
        SQL"""insert into value_enum values 
              (0,0,0,0,0,0,0),
             (1,1,1,1,1,1,1),
             (2,2,2,2,2,2,2),
             (3,null,null,null,null,null,null)"""
          .executeInsert()
      }
    }
  }
  override protected def afterAll(): Unit = {
    db.shutdown()
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
