package enumeratum.values

import enumeratum.Eval
import org.scalatest.{ FunSpec, Matchers }

import scala.reflect.ClassTag
import scala.util.Random

/**
 * Created by Lloyd on 8/30/16.
 *
 * Copyright 2016
 */
class ValueEnumJVMSpec extends FunSpec with Matchers {

  private def stringGenerator = Random.alphanumeric.grouped(10).toStream.map(_.mkString.replaceAll("[0-9]", "")).distinct

  /*
   Non-deterministically generates a bunch of different types of ValueEnums and tests the ability to resolve
   proper members by value
    */
  testValuesOf(Stream.continually(Random.nextInt()))
  testValuesOf(Stream.continually(Random.nextLong()), valueSuffix = "L")
  testValuesOf(Stream.continually(Random.nextInt()).collect { case i if i >= Short.MinValue && i <= Short.MaxValue => i.toShort })
  testValuesOf(stringGenerator, "\"", "\"")

  private def testValuesOf[A: ClassTag](valuesGenerator: => Stream[A], valuePrefix: String = "", valueSuffix: String = ""): Unit = {

    val typeName = implicitly[ClassTag[A]].runtimeClass.getSimpleName.capitalize

    describe(s"${typeName}Enum withValue") {

      it("should return proper members for valid values but throw otherwise") {
        (1 to 20).foreach { i =>
          val enumName = s"Generated${typeName}Enum$i"
          val names = stringGenerator.take(5)
          val values = valuesGenerator.distinct.take(5)
          val namesToValues = names.zip(values)
          val memberDefs = namesToValues.map { case (n, v) => s"""case object $n extends $enumName($valuePrefix$v$valueSuffix)""" }.mkString("\n\n")
          val objDef =
            s"""
               |import enumeratum.values._
               |
          |sealed abstract class $enumName(val value: $typeName) extends ${typeName}EnumEntry
               |
          |object $enumName extends ${typeName}Enum[$enumName] {
               |  val values = findValues
               |
          |  $memberDefs
               |}
               |$enumName
        """.stripMargin
          val obj = Eval.apply[ValueEnum[A, _ <: ValueEnumEntry[A]]](objDef)
          namesToValues.foreach {
            case (n, v) =>
              obj.withValue(v).toString shouldBe n
          }
          // filterNot is not lazy until 2.12
          valuesGenerator.filter(a => !values.contains(a)).take(5).foreach { invalidValue =>
            intercept[NoSuchElementException] {
              obj.withValue(invalidValue)
            }
          }
        }
      }

    }

  }

}
