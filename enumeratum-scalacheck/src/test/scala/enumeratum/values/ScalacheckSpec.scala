package enumeratum.values

import enumeratum.ScalacheckTest
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class ScalacheckSpec
    extends FunSpec
    with ScalaCheckDrivenPropertyChecks
    with Matchers
    with ScalacheckTest {

  import scalacheck._

  test[ByteEnumEntry, Bites]("ByteEnumEntry")
  test[CharEnumEntry, Alphabet]("CharEnumEntry")
  test[IntEnumEntry, LibraryItem]("IntEnumEntry")
  test[LongEnumEntry, ContentType]("LongEnumEntry")
  test[ShortEnumEntry, Drinks]("ShortEnumEntry")
  test[StringEnumEntry, OperatingSystem]("StringEnumEntry")

}
