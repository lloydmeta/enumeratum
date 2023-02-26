package enumeratum

import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.FixtureAnyFunSpec
import org.scalatest.matchers.should.Matchers._
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback

import scala.collection.immutable

class ScalikeJDBCEnumSpec extends FixtureAnyFunSpec with AutoRollback with BeforeAndAfterAll {

  sealed trait TrafficLight extends EnumEntry
  object TrafficLight extends ScalikeJDBCEnum[TrafficLight] {
    case object Red    extends TrafficLight
    case object Yellow extends TrafficLight
    case object Green  extends TrafficLight
    override val values: immutable.IndexedSeq[TrafficLight] = findValues
  }

  case class TrafficLightRow(id: Int, trafficLight: TrafficLight)
  object TrafficLightRow extends SQLSyntaxSupport[TrafficLightRow] {
    override val tableName = "traffic_table"
    def apply(rs: WrappedResultSet) = new TrafficLightRow(
      rs.int("id"),
      rs.get("traffic_light")
    )
  }

  override def beforeAll(): Unit = {
    Class.forName("org.h2.Driver")
    ConnectionPool.singleton("jdbc:h2:mem:scalikejdbcenumspec", "user", "pass")

    implicit val session: DBSession = AutoSession
    sql"""
    create table traffic_table (
      id integer not null primary key,
      traffic_light enum('Red', 'Yellow', 'Green')
    )
    """.execute.apply() shouldBe false
  }

  override def fixture(implicit session: DBSession): Unit = {
    sql"insert into traffic_table values (1, ${"Red"})".update.apply() shouldBe 1
    sql"insert into traffic_table values (2, ${"Green"})".update.apply() shouldBe 1
  }

  describe("select") {
    it("use SQLInterpolation") { implicit dbSession =>
      // exercise
      val Some(trafficLightRow: TrafficLightRow) = sql"select * from traffic_table where id = 1"
        .map(TrafficLightRow.apply)
        .single
        .apply()

      // verify
      trafficLightRow.id shouldBe 1
      trafficLightRow.trafficLight shouldBe TrafficLight.Red
    }

    it("use QueryDSL") { implicit dbSession =>
      // exercise
      val t = TrafficLightRow.syntax("t")
      val Some(trafficLightRow: TrafficLightRow) = withSQL {
        select.from(TrafficLightRow as t).where.eq(t.id, 1)
      }.map(TrafficLightRow.apply).single.apply()

      // verify
      trafficLightRow.id shouldBe 1
      trafficLightRow.trafficLight shouldBe TrafficLight.Red
    }
  }

  describe("insert") {
    it("use SQLInterpolation") { implicit dbSession =>
      // exercise
      sql"insert into traffic_table (id, traffic_light) values (3, ${"Green"})".update
        .apply() shouldBe 1

      // verify
      val Some(trafficLightRow: TrafficLightRow) = sql"select * from traffic_table where id = 3"
        .map(TrafficLightRow.apply)
        .single
        .apply()
      trafficLightRow.trafficLight shouldBe TrafficLight.Green
    }

    it("use QueryDSL") { implicit dbSession =>
      // exercise
      val c = TrafficLightRow.column
      applyUpdate {
        insert
          .into(TrafficLightRow)
          .namedValues(
            c.id           -> 3,
            c.trafficLight -> (TrafficLight.Green: TrafficLight)
          )
      } shouldBe 1

      // verify
      val t = TrafficLightRow.syntax("t")
      val Some(trafficLightRow: TrafficLightRow) = withSQL {
        select.from(TrafficLightRow as t).where.eq(t.id, 3)
      }.map(TrafficLightRow.apply).single.apply()
      trafficLightRow.trafficLight shouldBe TrafficLight.Green
    }
  }
}
