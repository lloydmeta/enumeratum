package enumeratum
import anorm.SqlParser._
import anorm._
import org.scalatest.{BeforeAndAfterAll, FreeSpec, Matchers}
import play.api.db.Databases

class AnormEnumSupportTest
    extends FreeSpec
    with Matchers
    with BeforeAndAfterAll
    with AnormEnumSupport {

  val db = Databases.inMemory()

  val red: TrafficLight = TrafficLight.Red

  "AnormEnumSupport" - {
    "allows creation of working ToStatement[_] and Column[_] for standard enums" - {
      "exact name" in {
        implicit val trafficLightToStatement = toStatementForEnum(TrafficLight)
        implicit val trafficLightColumn      = columnForEnum(TrafficLight)
        val id ~ trafficLight                = trafficLightInfo(red)
        id shouldBe 1
        trafficLight shouldBe red
      }
      "insensitive" in {
        implicit val trafficLightColumn = columnForEnumInsensitive(TrafficLight)
        db.withConnection { implicit c =>
          SQL"select value from traffic_light where id=6".as(scalar[TrafficLight].single)
        } shouldBe red
      }
      "uppercase" in {
        implicit val trafficLightToStatement = toStatementForEnumUppercase(TrafficLight)
        implicit val trafficLightColumn      = columnForEnumUppercase(TrafficLight)
        val id ~ trafficLight                = trafficLightInfo(red)
        id shouldBe 2
        trafficLight shouldBe red
      }
      "lowercase" in {
        implicit val trafficLightToStatement = toStatementForEnumLowercase(TrafficLight)
        implicit val trafficLightColumn      = columnForEnumLowercase(TrafficLight)
        val id ~ trafficLight                = trafficLightInfo(red)
        id shouldBe 3
        trafficLight shouldBe red
      }
    }
    "knows how to handle corner cases" - {
      "fail to get enum from NULL" in {
        a[AnormException] should be thrownBy db.withConnection { implicit c =>
          implicit val trafficLightColumn = columnForEnum(TrafficLight)
          SQL"select value from traffic_light where id=4".as(scalar[TrafficLight].single)
        }
      }
      "fail to get unsupported value" in {
        val exception = the[AnormException] thrownBy db.withConnection { implicit c =>
          implicit val trafficLightColumn = columnForEnum(TrafficLight)
          SQL"select value from traffic_light where id=5".as(scalar[TrafficLight].single)
        }
        exception.message should startWith("TypeDoesNotMatch")
      }
      "succeed when get option enum from NULL" in {
        db.withConnection { implicit c =>
          implicit val trafficLightColumn = columnForEnum(TrafficLight)
          SQL"select value from traffic_light where id=4".as(scalar[Option[TrafficLight]].single)
        } shouldBe empty
      }
    }
  }

  private def trafficLightInfo(trafficLight: TrafficLight)(
      implicit toStatement: ToStatement[TrafficLight],
      column: Column[TrafficLight]): Int ~ TrafficLight = {
    db.withConnection { implicit c =>
      SQL"select * from traffic_light where value=$trafficLight"
        .as((int("id") ~ get[TrafficLight]("value")).single)
    }
  }

  override protected def beforeAll(): Unit = {
    db.withConnection { implicit c =>
      SQL("""CREATE TABLE traffic_light (
            |   id INT NOT NULL,
            |   value VARCHAR(10)
            |)""".stripMargin)
        .execute()
      db.withConnection { implicit c =>
        implicit val toStatement = toStatementForEnum(TrafficLight)
        SQL"insert into traffic_light values (1,$red)".executeInsert()
      }
      db.withConnection { implicit c =>
        implicit val toStatement = toStatementForEnumUppercase(TrafficLight)
        SQL"insert into traffic_light values (2,$red)".executeInsert()
      }
      db.withConnection { implicit c =>
        implicit val toStatement = toStatementForEnumLowercase(TrafficLight)
        SQL"insert into traffic_light values (3,$red)".executeInsert()
      }
      db.withConnection { implicit c =>
        SQL"insert into traffic_light values (4,NULL),(5,'invalid'),(6,'rED')".executeInsert()
      }
    }
  }
  override protected def afterAll(): Unit = {
    db.shutdown()
  }
}

sealed trait TrafficLight extends EnumEntry
object TrafficLight extends Enum[TrafficLight] {
  case object Red    extends TrafficLight
  case object Yellow extends TrafficLight
  case object Green  extends TrafficLight

  val values = findValues
}
