package enumeratum

import org.scalacheck.Cogen

trait CogenInstances {

  /**
    * `Enum` context bound is unused but can drastically reduce compilation time.
    * See https://github.com/nrinaudo/kantan.codecs/blob/bb74def19e94ce4f14330100b467c3fc9271068d/enumeratum/core/src/main/scala/kantan/codecs/enumeratum/values/ValueEnumInstances.scala#L82
    */
  implicit def cogenEnumEntry[EnumType <: EnumEntry: Enum]: Cogen[EnumType] =
    Cogen[String].contramap(_.entryName)

}
