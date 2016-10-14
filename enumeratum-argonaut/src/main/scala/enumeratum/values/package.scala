package enumeratum

import argonaut._
import Argonaut._

/**
  * Created by alonsodomin on 14/10/2016.
  */
package object values {

  implicit val argonautByteEncoder: EncodeJson[Byte] = EncodeJson { byte =>
    jNumber(byte.toShort)
  }

  implicit val argonautByteDecoder: DecodeJson[Byte] = DecodeJson { cursor =>
    cursor.as[Short].map(_.toByte)
  }

}
