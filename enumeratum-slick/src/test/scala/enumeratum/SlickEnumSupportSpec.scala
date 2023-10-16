package enumeratum

import enumeratum.values.SlickValueEnumSupport
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class SlickEnumSupportSpec
    extends AnyFreeSpec
    with ScalaFutures
    with Matchers
    with BeforeAndAfterAll {

  trait TrafficLightRepository extends SlickEnumSupport with SlickValueEnumSupport {

    import profile.api._

    implicit val trafficLightColumnType: profile.BaseColumnType[TrafficLight] =
      mappedColumnTypeForEnum(TrafficLight)
    val trafficLightUpperColumnType = mappedColumnTypeForUppercaseEnum(TrafficLight)
    val trafficLightLowerColumnType = mappedColumnTypeForLowercaseEnum(TrafficLight)

    val trafficLightSetParamName   = setParameterForEnum(TrafficLight)
    val trafficLightGetResultName  = getResultForEnum(TrafficLight)
    val trafficLightSetParamUpper  = setParameterForEnumUppercase(TrafficLight)
    val trafficLightGetResultUpper = getResultForEnumUppercase(TrafficLight)
    val trafficLightSetParamLower  = setParameterForEnumLowercase(TrafficLight)
    val trafficLightGetResultLower = getResultForEnumLowercase(TrafficLight)

    type TrafficLightRow = (String, TrafficLight, TrafficLight, TrafficLight)
    class TrafficLightTable(tag: Tag) extends Table[TrafficLightRow](tag, "traffic_light") {
      def id =
        column[String]("id", O.PrimaryKey)
      def trafficLightByName =
        column[TrafficLight]("traffic_light_name")
      def trafficLightByNameUpper =
        column[TrafficLight]("traffic_light_name_upper")(trafficLightUpperColumnType)
      def trafficLightByNameLower =
        column[TrafficLight]("traffic_light_name_lower")(trafficLightLowerColumnType)

      def * = (
        id,
        trafficLightByName,
        trafficLightByNameUpper,
        trafficLightByNameLower
      )

    }
    val lights = TableQuery[TrafficLightTable]
  }
  class ConcreteRepository(val profile: slick.jdbc.H2Profile) extends TrafficLightRepository

  val repo = new ConcreteRepository(slick.jdbc.H2Profile)
  import repo.lights
  import repo.profile.api._
  val db = Database.forURL(
    url = "jdbc:h2:mem:test",
    driver = "org.h2.Driver",
    keepAliveConnection = true
  )

  override def beforeAll(): Unit = {
    db.run(lights.schema.create).futureValue(Timeout(Span(1, Second)))
  }

  override def afterAll(): Unit = {
    db.close()
  }

  "SlickEnumSupport" - {
    "allows creation of working column mappers for standard and value enums" - {
      val redLight =
        ("1", TrafficLight.Red, TrafficLight.Red, TrafficLight.Red)
      "Insertion works" in {
        db.run(lights += redLight).futureValue shouldBe 1
      }
      "Querying works" in {
        import repo.trafficLightColumnType
        db.run(lights.result.head).futureValue shouldBe redLight
        db.run(lights.filter(_.trafficLightByName === (TrafficLight.Red: TrafficLight)).result.head)
          .futureValue shouldBe redLight
      }
      "Name columns are actually mapped as specified" - {
        "exact name" in {
          val selectRedAsName =
            sql"""
                 select "traffic_light_name" from "traffic_light" where "id" = '1'
              """.as[String]
          val result = db.run(selectRedAsName).futureValue.head
          result shouldBe TrafficLight.Red.entryName
        }
        "uppercase" in {
          val selectRedAsUppercaseName =
            sql"""
                 select "traffic_light_name_upper" from "traffic_light" where "id" = '1'
              """.as[String]
          val result = db.run(selectRedAsUppercaseName).futureValue.head
          result shouldBe TrafficLight.Red.entryName.toUpperCase
        }
        "lowercase" in {
          val selectRedAsLowercaseName =
            sql"""
                 select "traffic_light_name_lower" from "traffic_light" where "id" = '1'
              """.as[String]
          val result = db.run(selectRedAsLowercaseName).futureValue.head
          result shouldBe TrafficLight.Red.entryName.toLowerCase
        }
      }
    }
    "allows creation of working SetParameter[_] and GetResult[_] for standard enums" - {
      "exact name" in {
        implicit val setParam  = repo.trafficLightSetParamName
        implicit val getResult = repo.trafficLightGetResultName
        val selectRedByName =
          sql"""
              select "traffic_light_name" from "traffic_light"
              where "traffic_light_name" = ${TrafficLight.Red}
            """.as[TrafficLight]
        db.run(selectRedByName).futureValue.head shouldBe TrafficLight.Red
      }
      "uppercase" in {
        implicit val setParam  = repo.trafficLightSetParamUpper
        implicit val getResult = repo.trafficLightGetResultUpper
        val selectRedByUppercaseName =
          sql"""
              select "traffic_light_name_upper" from "traffic_light"
              where "traffic_light_name_upper" = ${TrafficLight.Red}
            """.as[TrafficLight]
        db.run(selectRedByUppercaseName).futureValue.head shouldBe TrafficLight.Red
      }
      "lowercase" in {
        implicit val setParam  = repo.trafficLightSetParamLower
        implicit val getResult = repo.trafficLightGetResultLower
        val selectRedByLowercaseName =
          sql"""
              select "traffic_light_name_lower" from "traffic_light"
              where "traffic_light_name_lower" = ${TrafficLight.Red}
            """.as[TrafficLight]
        db.run(selectRedByLowercaseName).futureValue.head shouldBe TrafficLight.Red
      }
    }
  }
}

sealed trait TrafficLight extends EnumEntry
object TrafficLight extends Enum[TrafficLight] {
  case object Red    extends TrafficLight
  case object Yellow extends TrafficLight
  case object Green  extends TrafficLight

  val values = findValues
}
