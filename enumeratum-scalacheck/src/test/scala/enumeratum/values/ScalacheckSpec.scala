package enumeratum.values

import enumeratum.ScalacheckTest
import org.scalatest.{FunSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class ScalacheckSpec
    extends FunSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with ScalacheckTest {

  import scalacheck._

  test[ByteEnumEntry, Bites]
  test[CharEnumEntry, Alphabet]
  test[IntEnumEntry, LibraryItem]
  test[LongEnumEntry, ContentType]
  test[ShortEnumEntry, Drinks]
  test[StringEnumEntry, OperatingSystem]

}
