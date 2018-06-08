package enumeratum.values

import enumeratum.{SlickEnumSupport, TrafficLight, TrafficLightByInt}
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.{BeforeAndAfterAll, FreeSpec, Matchers}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Span}
import slick.jdbc.SetParameter

class SlickEnumSpec extends FreeSpec with ScalaFutures with Matchers with BeforeAndAfterAll {

  trait TrafficLightRepository extends SlickEnumSupport with SlickValueEnumSupport {

    import profile.api._

    implicit val trafficLightColumnType      = mappedColumnTypeForEnum(TrafficLight)
    implicit val trafficLightByNumColumnType = mappedColumnTypeForValueEnum(TrafficLightByInt)
    val trafficLightUpperColumnType          = mappedColumnTypeForUppercaseEnum(TrafficLight)
    val trafficLightLowerColumnType          = mappedColumnTypeForLowercaseEnum(TrafficLight)

    val trafficLightSetParamName  = setParameterForEnum(TrafficLight)
    val trafficLightSetParamUpper = setParameterForUppercaseEnum(TrafficLight)
    val trafficLightSetParamLower = setParameterForLowercaseEnum(TrafficLight)
    val trafficLightSetParamNumber = setParameterForIntEnum(TrafficLightByInt)

    type TrafficLightRow = (String, TrafficLight, TrafficLight, TrafficLight, TrafficLightByInt)
    class TrafficLightTable(tag: Tag) extends Table[TrafficLightRow](tag, "traffic_light") {
      def id =
        column[String]("id", O.PrimaryKey)
      def trafficLightByName =
        column[TrafficLight]("traffic_light_name")
      def trafficLightByNameUpper =
        column[TrafficLight]("traffic_light_name_upper")(trafficLightUpperColumnType)
      def trafficLightByNameLower =
        column[TrafficLight]("traffic_light_name_lower")(trafficLightLowerColumnType)
      def trafficLightByNum =
        column[TrafficLightByInt]("traffic_light_number")

      def * = (
        id,
        trafficLightByName,
        trafficLightByNameUpper,
        trafficLightByNameLower,
        trafficLightByNum
      )

    }
    val lights = TableQuery[TrafficLightTable]
  }
  class ConcreteRepository(val profile: slick.jdbc.H2Profile) extends TrafficLightRepository

  val repo = new ConcreteRepository(slick.jdbc.H2Profile)
  import repo.profile.api._
  import repo.lights
  val db = Database.forURL(
    url = "jdbc:h2:mem:test",
    driver = "org.h2.Driver",
    keepAliveConnection = true
  )

  override def beforeAll(): Unit = {
    db.run(lights.schema.create).futureValue(Timeout(Span(500, Millis)))
  }

  override def afterAll(): Unit = {
    db.close()
  }

  "SlickEnumSupport" - {
    "allows creation of working column mappers for standard and value enums" - {
      val redLight =
        ("1", TrafficLight.Red, TrafficLight.Red, TrafficLight.Red, TrafficLightByInt.Red)
      "Insertion works" in {
        db.run(lights += redLight).futureValue shouldBe 1
      }
      "Querying works" in {
        db.run(lights.result.head).futureValue shouldBe redLight
        import repo.trafficLightColumnType
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
      "Value columns are actually mapped as specified" in {
        val selectRedAsInteger =
          sql"""select "traffic_light_number" from "traffic_light" where "id" = '1'""".as[Int]
        db.run(selectRedAsInteger).futureValue.head shouldBe TrafficLightByInt.Red.value
      }
    }
    "allows creation of working SetParameters for standard enums" - {
      "exact name" in {
        implicit val setParam = repo.trafficLightSetParamName
        val selectRedByName =
          sql"""
              select "traffic_light_name" from "traffic_light"
              where "traffic_light_name" = ${TrafficLight.Red}
            """.as[String]
        db.run(selectRedByName).futureValue.head shouldBe TrafficLight.Red.entryName
      }
      "uppercase" in {
        implicit val setParam = repo.trafficLightSetParamUpper
        val selectRedByUppercaseName =
          sql"""
              select "traffic_light_name_upper" from "traffic_light"
              where "traffic_light_name_upper" = ${TrafficLight.Red}
            """.as[String]
        db.run(selectRedByUppercaseName)
          .futureValue
          .head shouldBe TrafficLight.Red.entryName.toUpperCase
      }
      "lowercase" in {
        implicit val setParam = repo.trafficLightSetParamLower
        val selectRedByLowercaseName =
          sql"""
              select "traffic_light_name_lower" from "traffic_light"
              where "traffic_light_name_lower" = ${TrafficLight.Red}
            """.as[String]
        db.run(selectRedByLowercaseName)
          .futureValue
          .head shouldBe TrafficLight.Red.entryName.toLowerCase
      }
    }
    "allows creation of working SetParameters for value enums" in {
      implicit val setParam = repo.trafficLightSetParamNumber
      val selectRedByNumber =
        sql"""
          select "traffic_light_number" from "traffic_light"
          where "traffic_light_number" = ${TrafficLightByInt.Red}
        """.as[Int]
      db.run(selectRedByNumber).futureValue.head shouldBe TrafficLightByInt.Red.value
    }
  }
}
