import scala.util.Random

import sbt._
import Keys._

object CoreJVMTest {
  lazy val testsGenerator = Def.task[Seq[File]] {
    val managed = (Test / sourceManaged).value

    generateEnumTests(managed) ++ generateValueEnumTest[Int](
      managed,
      Iterator.continually(Random.nextInt())
    ) ++ generateValueEnumTest[Long](
      managed,
      Iterator.continually(Random.nextLong()),
      valueSuffix = "L"
    ) ++ generateValueEnumTest[Short](
      managed,
      Iterator
        .continually(Random.nextInt(Short.MaxValue - Short.MinValue) + Short.MinValue)
        .map(_.toShort)
    ) ++ generateValueEnumTest[Byte](
      managed,
      Iterator
        .continually(Random.nextInt(Byte.MaxValue - Byte.MinValue) + Byte.MinValue)
        .map(_.toByte)
    ) ++ generateValueEnumTest[String](
      managed,
      Random.alphanumeric.grouped(10).map(_.mkString),
      "\"",
      "\""
    ) ++ generateValueEnumTest[Char](
      managed,
      Iterator.continually(Random.nextPrintableChar()).filter(c => Character.isAlphabetic(c.toInt)),
      "'",
      "'"
    )

  }

  private def generateEnumTests(outdir: File): Seq[File] = {
    val bf = outdir / "EnumBaseSpec.scala"

    IO.writer[Seq[File]](bf, "", IO.defaultCharset, false) { w0 =>
      w0.append("""package generated

trait EnumBaseSpec
  extends org.scalatest.funspec.AnyFunSpec 
  with org.scalatest.matchers.should.Matchers
""")

      val res = (1 to 100).flatMap { i =>
        val enumName = s"Enum${i}"

        val ef = outdir / s"${enumName}.scala"

        IO.writer[Seq[File]](ef, "", IO.defaultCharset, false) { w1 =>
          // Generate enum file

          w1.append(s"""package generated

import enumeratum._

sealed trait ${enumName} extends EnumEntry

object ${enumName} extends Enum[${enumName}] {
  val values = findValues

""")

          val members = Random.shuffle(1 to Random.nextInt(20)).map { n =>
            val nme = s"Member$n"

            w1.append(s"  case object ${nme} extends ${enumName}\n")

            nme
          }

          w1.append(s"""}
""")

          // Generate tests in separate file/trait
          val tf = outdir / s"${enumName}Test.scala"

          w0.append(s"  with ${enumName}Test\n")

          IO.writer[Seq[File]](tf, "", IO.defaultCharset, false) { w2 =>
            val expectedNames = members.map('"' + _ + '"').mkString(", ")

            val expectedMembers = members.map(enumName + '.' + _).mkString(", ")

            w2.append(s"""package generated

trait ${enumName}Test { _spec: EnumBaseSpec with enumeratum.EnumJVMSpec =>
  describe("${enumName}.findValues") {
    // This is a fairly intense test.
    it("should be in the same order as declaration on objects") {
      ${enumName}.values.map(_.entryName).toSeq shouldBe Seq($expectedNames)

      ${enumName}.values.toSeq shouldBe Seq($expectedMembers)
    }
  }
}
""")

            Seq(ef, tf)
          }
        }
      }

      w0.append(""" { self: enumeratum.EnumJVMSpec =>
}""")

      bf +: res
    }
  }

  // ---

  private def generateValueEnumTest[A](
      outdir: File,
      valuesGenerator: => Iterator[A],
      valuePrefix: String = "",
      valueSuffix: String = ""
  )(implicit cls: scala.reflect.ClassTag[A]): Seq[File] = {
    val typeName = cls.runtimeClass.getSimpleName.capitalize

    def renderValue(v: A): String = valuePrefix + v.toString + valueSuffix

    @annotation.tailrec
    def genValues(rem: Int, out: IndexedSeq[A]): IndexedSeq[A] = {
      if (rem == 0) {
        out.reverse
      } else {
        val v = valuesGenerator.next()

        if (!out.contains(v)) {
          genValues(rem - 1, v +: out)
        } else {
          genValues(rem, out)
        }
      }
    }

    //
    val bf = outdir / s"${typeName}ValueEnumBaseSpec.scala"

    IO.writer[Seq[File]](bf, "", IO.defaultCharset, false) { w0 =>
      w0.append(s"""package generated

trait ${typeName}ValueEnumBaseSpec
  extends org.scalatest.funspec.AnyFunSpec 
  with org.scalatest.matchers.should.Matchers
""")

      val res = (1 to 20).flatMap { i =>
        val enumName      = s"${typeName}Enum$i"
        val names         = stringGenerator(5)
        val values        = genValues(5, IndexedSeq.empty)
        val namesToValues = names.zip(values)

        // Generate value enum file
        val ef = outdir / s"${enumName}.scala"

        IO.writer[Seq[File]](ef, "", IO.defaultCharset, false) { w1 =>
          w1.append(s"""package generated

import enumeratum.values._

sealed abstract class $enumName(
  val value: $typeName
) extends ${typeName}EnumEntry

object $enumName extends ${typeName}Enum[$enumName] {
  val values = findValues

""")

          namesToValues.foreach { case (n, v) =>
            w1.append(s"  case object $n extends $enumName($valuePrefix$v$valueSuffix)\n\n")
          }

          w1.append("}\n")

          // Generate test in separate file
          val tf = outdir / s"${enumName}Test.scala"

          w0.append(s"  with ${enumName}Test\n")

          IO.writer[Seq[File]](tf, "", IO.defaultCharset, false) { w2 =>
            w2.append(s"""package generated

trait ${enumName}Test {
  _spec: ${typeName}ValueEnumBaseSpec with enumeratum.values.ValueEnumJVMSpec =>

  describe("${enumName} withValue") {
    it("should return proper members for valid values but throw otherwise") {
""")

            namesToValues.foreach { case (n, v) =>
              val value = renderValue(v)

              w2.append(s"""      ${enumName}.withValue($value) shouldBe ${enumName}.${n}

""")
            }

            valuesGenerator.filter(a => !values.contains(a)).take(5).foreach { invalidValue =>
              val value = renderValue(invalidValue)

              w2.append(s"""      intercept[NoSuchElementException] {
        ${enumName}.withValue($value)
      }

""")
            }

            w2.append("""    }
  }
}
""")

            Seq(ef, tf)
          }
        }
      }

      w0.append(""" { self: enumeratum.values.ValueEnumJVMSpec =>
}""")

      bf +: res
    }
  }

  private def stringGenerator(n: Int) = scala.util.Random.alphanumeric
    .grouped(10)
    .toStream
    .map(_.mkString.replaceAll("[0-9]", ""))
    .distinct
    .take(n)
    .toSeq
}
