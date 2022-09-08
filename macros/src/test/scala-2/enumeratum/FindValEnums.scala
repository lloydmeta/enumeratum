package enumeratum

import scala.language.experimental.macros

/** Created by Lloyd on 1/4/17.
  *
  * Copyright 2017
  */
object FindValEnums {
  def apply[A]: IndexedSeq[A] = macro ValueEnumMacros.findIntValueEntriesImpl[A]
}
