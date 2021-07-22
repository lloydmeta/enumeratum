package enumeratum.values

import play.api.mvc.{PathBindable, QueryStringBindable}

/** Created by Lloyd on 4/13/16.
  *
  * Copyright 2016
  */
object UrlBinders {

  /** Returns a [[PathBindable]] for the provided ValueEnum and base [[PathBindable]]
    */
  def pathBinder[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]
  )(implicit
      baseBindable: PathBindable[ValueType]
  ): PathBindable[EntryType] =
    new PathBindable[EntryType] {
      def bind(key: String, value: String): Either[String, EntryType] =
        baseBindable.bind(key, value).right.flatMap { b =>
          val maybeBound = enum.withValueOpt(b)
          maybeBound match {
            case Some(obj) => Right(obj)
            case None =>
              Left(s"Unknown value supplied for ${enum.toString} '" + value + "'")
          }
        }

      def unbind(key: String, value: EntryType): String =
        baseBindable.unbind(key, value.value)
    }

  /** Returns a [[QueryStringBindable]] for the provided ValueEnum and base [[PathBindable]]
    */
  def queryBinder[ValueType, EntryType <: ValueEnumEntry[ValueType]](
      enum: ValueEnum[ValueType, EntryType]
  )(implicit
      baseBindable: QueryStringBindable[ValueType]
  ): QueryStringBindable[EntryType] =
    new QueryStringBindable[EntryType] {
      def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, EntryType]] = {
        baseBindable
          .bind(key, params)
          .map(_.right.flatMap { s =>
            val maybeBound = enum.withValueOpt(s)
            maybeBound match {
              case Some(obj) => Right(obj)
              case None      => Left(s"Unknown value supplied for ${enum.toString} '${s.toString}'")
            }
          })
      }

      def unbind(key: String, entry: EntryType): String =
        baseBindable.unbind(key, entry.value)
    }

}
