package enumeratum

import slick.jdbc.{PositionedParameters, SetParameter}

import scala.reflect.ClassTag

/**
  * Provides helpers for building Slick typeclass instances for an [[Enum]]
  *
  * Can also be used via the companion object
  */
trait SlickEnum {

  def buildSetParameterTypeForEnum[E <: EnumEntry](
      nameFn: String => String = identity): SetParameter[E] = {
    new SetParameter[E] {
      def apply(e: E, pp: PositionedParameters): Unit = {
        val transformedName = nameFn(e.entryName)
        pp.setString(transformedName)
      }
    }
  }

  def buildMappedColumnTypeForEnum[E <: EnumEntry, S](enum: Enum[E],
                                                      eToString: E => S,
                                                      stringToE: S => E,
                                                      profile: slick.profile.RelationalProfile)(
      implicit tag: ClassTag[E],
      sMapper: profile.BaseColumnType[S]): profile.BaseColumnType[E] = {
    profile.MappedColumnType.base[E, S](
      { eToString(_) },
      { stringToE }
    )
  }

}

object SlickEnum extends SlickEnum
