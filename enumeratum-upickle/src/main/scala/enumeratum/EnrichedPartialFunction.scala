package enumeratum

/**
  * Created by Lloyd on 4/14/16.
  *
  * Copyright 2016
  */
object EnrichedPartialFunction {

  /**
    * From http://stackoverflow.com/questions/23024626/compose-partial-functions
    */
  implicit class PartialFunctionOps[A, B](val pf: PartialFunction[A, B]) extends AnyVal {
    def andThenPartial[C](that: PartialFunction[B, C]): PartialFunction[A, C] =
      Function.unlift(pf.lift(_) flatMap that.lift)
  }

}
