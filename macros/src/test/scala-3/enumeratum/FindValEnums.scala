package enumeratum

/** Created by Lloyd on 1/4/17.
  *
  * Copyright 2017
  */
object FindValEnums {
  inline def apply[A]: IndexedSeq[A] = ${ ValueEnumMacros.findIntValueEntriesImpl[A] }
}
